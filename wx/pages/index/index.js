//index.js
//获取应用实例
var Auth = require('../../utils/auth.js');
const app = getApp()

Page({
  data: {
    free_minutes: 0,
    fee_per_hours: 0
  },
  //事件处理函数
  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad: function () {
    var that = this
    wx.request({
      url: "http://localhost:8088/api/park",
      header: {
        'Content-Type': 'application/json'
      },
      success: function (result) {
        console.log(result.data, result.data.length)
        var use = [], empty = []
        that.setData({
          free_minutes: result.data.free_minutes,
          fee_per_hours: result.data.fee_per_hours
        })
      }, fail(res) {
        console.log(res)
      }
    })
    Auth.wxLogin()
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse){
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },
  getUserInfo: function(e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  }
})
