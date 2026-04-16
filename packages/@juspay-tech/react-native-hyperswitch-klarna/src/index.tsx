import React, { forwardRef, useImperativeHandle, useRef, useState } from 'react';
import {
  findNodeHandle,
  NativeModules,
  Platform,
  requireNativeComponent,
  UIManager,
  type HostComponent,
  type NativeSyntheticEvent,
  type ViewProps,
} from 'react-native';

const VIEW_NAME = 'HyperswitchKlarnaPaymentView';

export interface KlarnaAuthorizedParams {
  authorized: boolean;
  approved: boolean;
  authToken?: string;
  errorMessage?: string;
  finalizeRequired?: boolean;
}

export interface KlarnaResizeParams {
  width: number;
  height: number;
}

export interface KlarnaPaymentViewNativeProps extends ViewProps {
  category?: string;
  returnURL?: string;
  onInitialized?: (event: NativeSyntheticEvent<{}>) => void;
  onLoaded?: (event: NativeSyntheticEvent<{}>) => void;
  onAuthorized?: (event: NativeSyntheticEvent<KlarnaAuthorizedParams>) => void;
  onResize?: (event: NativeSyntheticEvent<KlarnaResizeParams>) => void;
}

export interface KlarnaPaymentViewProps {
  category?: string;
  paymentMethod?: string;
  children?: React.ReactNode;
  onInitialized?: () => void;
  onLoaded?: () => void;
  onAuthorized?: (event: { nativeEvent: KlarnaAuthorizedParams }) => void;
  style?: ViewProps['style'];
}

export interface KlarnaPaymentViewHandle {
  initialize: (clientToken: string, returnURL?: string | null) => void;
  load: () => void;
  authorize: () => void;
}

const NativeKlarnaPaymentView: HostComponent<KlarnaPaymentViewNativeProps> =
  requireNativeComponent<KlarnaPaymentViewNativeProps>(VIEW_NAME);

const getCommands = () => UIManager.getViewManagerConfig(VIEW_NAME)?.Commands;

const dispatchCommand = (
  node: number | null,
  commandName: 'initialize' | 'load' | 'authorize',
  args: Array<unknown>
) => {
  if (node == null) return;
  if (Platform.OS === 'ios') {
    const module = NativeModules[VIEW_NAME];
    if (module && typeof module[commandName] === 'function') {
      module[commandName](node, ...args);
      return;
    }
  }
  const commands = getCommands();
  const commandId = commands ? commands[commandName] : undefined;
  if (commandId !== undefined) {
    UIManager.dispatchViewManagerCommand(node, commandId, args);
  }
};

const KlarnaPaymentView = forwardRef<KlarnaPaymentViewHandle, KlarnaPaymentViewProps>(
  (props, ref) => {
    const nativeRef = useRef<React.ComponentRef<typeof NativeKlarnaPaymentView>>(null);
    const category = props.category ?? props.paymentMethod ?? 'klarna';
    const [measuredHeight, setMeasuredHeight] = useState<number | null>(null);

    useImperativeHandle(ref, () => ({
      initialize: (clientToken: string, returnURL?: string | null) => {
        const node = findNodeHandle(nativeRef.current);
        dispatchCommand(node, 'initialize', [clientToken, returnURL ?? null]);
      },
      load: () => {
        const node = findNodeHandle(nativeRef.current);
        dispatchCommand(node, 'load', []);
      },
      authorize: () => {
        const node = findNodeHandle(nativeRef.current);
        dispatchCommand(node, 'authorize', []);
      },
    }));

    const dynamicStyle =
      Platform.OS === 'ios' && measuredHeight != null ? { height: measuredHeight } : null;

    return (
      <NativeKlarnaPaymentView
        ref={nativeRef}
        category={category}
        style={[props.style, dynamicStyle]}
        onInitialized={() => props.onInitialized?.()}
        onLoaded={() => props.onLoaded?.()}
        onAuthorized={(event: NativeSyntheticEvent<KlarnaAuthorizedParams>) =>
          props.onAuthorized?.({ nativeEvent: event.nativeEvent })
        }
        onResize={(event: NativeSyntheticEvent<KlarnaResizeParams>) =>
          setMeasuredHeight(event.nativeEvent.height)
        }
      />
    );
  }
);

export default KlarnaPaymentView;

export const isAvailable = !!UIManager.getViewManagerConfig(VIEW_NAME);
