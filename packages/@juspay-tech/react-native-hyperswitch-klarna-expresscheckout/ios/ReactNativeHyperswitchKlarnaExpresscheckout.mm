#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(HyperswitchKlarnaExpressCheckout, NSObject)

RCT_EXTERN_METHOD(finalize:(NSString *)clientToken
                  callback:(RCTResponseSenderBlock)callback)

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

@end


@interface RCT_EXTERN_MODULE(KlarnaExpressCheckoutButton, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(clientToken, NSString)
RCT_EXPORT_VIEW_PROPERTY(sessionData, NSString)
RCT_EXPORT_VIEW_PROPERTY(autoFinalize, BOOL)
RCT_EXPORT_VIEW_PROPERTY(collectShippingAddress, BOOL)
RCT_EXPORT_VIEW_PROPERTY(locale, NSString)
RCT_EXPORT_VIEW_PROPERTY(environment, NSString)
RCT_EXPORT_VIEW_PROPERTY(region, NSString)
RCT_EXPORT_VIEW_PROPERTY(theme, NSString)
RCT_EXPORT_VIEW_PROPERTY(buttonShape, NSString)
RCT_EXPORT_VIEW_PROPERTY(buttonStyleValue, NSString)
RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(loggingLevel, NSString)
RCT_EXPORT_VIEW_PROPERTY(onAuthorized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)

@end
