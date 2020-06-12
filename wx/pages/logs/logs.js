var app = getApp();
Page({
    data: {
        postsList: [],
        userInfo: {},
        hasUserInfo: false,
        canIUse: wx.canIUse('button.open-type.getUserInfo'),
        showerror: "none",
        shownodata: "none",
        openid: '',
        isLoginPopup: false
    },

    /** 生命周期函数--监听页面加载 */
    onLoad: function (options) {
        console.log(app.globalData.userInfo)
    },

    onShow: function () {
        console.log(app.globalData.userInfo)
    }, 

    getUserInfo: function (e) {/* console.log(e)*/
        var that = this;
        // console.log(e);
        app.globalData.userInfo = e.detail.userInfo;
        this.setData({userInfo: e.detail.userInfo, hasUserInfo: true})
        console.log(app.globalData.openid, app.globalData.sig);
        //向小程序端发送请求 发送用户昵称
        console.log(app.globalData.userInfo.nickName)
        wx.request({
            url: app.globalData.url + 'info',
            data: {openid: app.globalData.openid, sig: app.globalData.sig,name:app.globalData.userInfo.nickName},
            method: 'POST', /* OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT*/
            header: {'content-type': 'application/x-www-form-urlencoded'}, /* 设置请求的 header*/
            success: function (res) {
                console.log("发送nickname成功");
            },
            fail: function () {
                console.log("index.js wx.request CheckCallUser fail");
            },
        })
    }
})