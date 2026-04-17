import {
  NativeModules,
  Platform,
  requireNativeComponent,
  type HostComponent,
  type NativeSyntheticEvent,
  type ViewProps,
} from 'react-native';

const LINKING_ERROR =
  `The package '@juspay-tech/react-native-hyperswitch-klarna-expresscheckout' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const HyperswitchKlarnaExpressCheckout =
  NativeModules.HyperswitchKlarnaExpressCheckout
    ? NativeModules.HyperswitchKlarnaExpressCheckout
    : new Proxy(
        {},
        {
          get() {
            throw new Error(LINKING_ERROR);
          },
        }
      );

export const isAvailable = Boolean(NativeModules.HyperswitchKlarnaExpressCheckout);

export type KlarnaExpressCheckoutEnvironment = 'production' | 'playground';
export type KlarnaExpressCheckoutRegion = 'NA' | 'EU' | 'OC';
export type KlarnaExpressCheckoutTheme = 'light' | 'dark' | 'auto';
export type KlarnaExpressCheckoutButtonShape =
  | 'rounded_rect'
  | 'rectangle'
  | 'pill';
export type KlarnaExpressCheckoutButtonStyleValue = 'filled' | 'outlined';
export type KlarnaExpressCheckoutLoggingLevel = 'off' | 'error' | 'verbose';

export type KlarnaExpressCheckoutAuthorizationResponse = {
  approved: boolean;
  showForm: boolean;
  finalizeRequired: boolean;
  authorizationToken?: string;
  clientToken?: string;
  sessionId?: string;
  collectedShippingAddress?: string;
  merchantReference1?: string;
  merchantReference2?: string;
};

export type KlarnaExpressCheckoutErrorResponse = {
  name: string;
  message: string;
  isFatal: boolean;
  sessionId?: string;
};

export type KlarnaExpressCheckoutButtonProps = ViewProps & {
  clientToken: string;
  sessionData?: string;
  autoFinalize?: boolean;
  collectShippingAddress?: boolean;
  locale?: string;
  environment?: KlarnaExpressCheckoutEnvironment;
  region?: KlarnaExpressCheckoutRegion;
  theme?: KlarnaExpressCheckoutTheme;
  buttonShape?: KlarnaExpressCheckoutButtonShape;
  buttonStyleValue?: KlarnaExpressCheckoutButtonStyleValue;
  returnUrl?: string;
  loggingLevel?: KlarnaExpressCheckoutLoggingLevel;
  onAuthorized?: (
    event: NativeSyntheticEvent<KlarnaExpressCheckoutAuthorizationResponse>
  ) => void;
  onError?: (
    event: NativeSyntheticEvent<KlarnaExpressCheckoutErrorResponse>
  ) => void;
};

export const KlarnaExpressCheckoutButton: HostComponent<KlarnaExpressCheckoutButtonProps> =
  requireNativeComponent<KlarnaExpressCheckoutButtonProps>(
    'KlarnaExpressCheckoutButton'
  );

export function finalize(
  clientToken: string,
  callback: (response: KlarnaExpressCheckoutAuthorizationResponse) => void
): void {
  if (!NativeModules.HyperswitchKlarnaExpressCheckout) {
    callback({
      approved: false,
      showForm: false,
      finalizeRequired: false,
    });
    return;
  }
  HyperswitchKlarnaExpressCheckout.finalize(clientToken, callback);
}
