#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>

@interface RCT_EXTERN_MODULE(HyperswitchKlarnaPaymentView, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(category, NSString)
RCT_EXPORT_VIEW_PROPERTY(returnURL, NSString)
RCT_EXPORT_VIEW_PROPERTY(onInitialized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoaded, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAuthorized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onResize, RCTDirectEventBlock)

RCT_EXTERN_METHOD(initialize:(nonnull NSNumber *)node
                  clientToken:(NSString *)clientToken
                  returnURL:(NSString *)returnURL)

RCT_EXTERN_METHOD(load:(nonnull NSNumber *)node)

RCT_EXTERN_METHOD(authorize:(nonnull NSNumber *)node)

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

@end
