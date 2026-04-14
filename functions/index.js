const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendOrderNotification = functions.firestore
    .document("orders/{orderId}")
    .onCreate(async (snap, context) => {
      const payload = {
        notification: {
          title: "Đơn hàng mới",
          body: "Bạn vừa đặt hàng thành công!",
        },
      };

      try {
        await admin.messaging().sendToTopic("all", payload);
        console.log("Gửi notification thành công");
      } catch (error) {
        console.log("Lỗi gửi notification:", error);
      }
    });
