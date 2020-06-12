//index.js
//获取应用实例
import * as echarts from '../../ec-canvas/echarts';

const app = getApp()
var interval

// var empty = [], use = [];

function setOption(chart, empty, use) {
    const option = {
        xAxis: {show: false},
        yAxis: {show: false},
        title: {
            text: '车位使用状况分布图'
        },
        animation: false,
        legend: {
            right: 10,
            data: ['占用', '空闲']
        },
        series: [
            {
                name: '占用',
                symbolSize: 20,
                data: use,
                type: 'scatter',
                showAllSymbol: true,
                symbol: 'rect',
                emphasis: {
                    label: {
                        show: true,
                        formatter: function (param) {
                            return param.data[2];
                        },
                        position: 'top'
                    }
                }
            },
            {
                name: '空闲',
                symbolSize: 20,
                data: empty,
                type: 'scatter',
                symbol: 'rect',
                emphasis: {
                    label: {
                        show: true,
                        formatter: function (param) {
                            return param.data[2];
                        },
                        position: 'top'
                    }
                }
            }]
    };

    chart.setOption(option);
}

Page({
    onReady: function () {
        // 获取组件
        this.ecComponent = this.selectComponent('#mychart-dom-bar');
    },

    data: {
        ec: {
            // 将 lazyLoad 设为 true 后，需要手动初始化图表
            lazyLoad: true
        },
        isLoaded: false,
        isDisposed: false,
        empty: [],
        use: [],
        parkCount: 0
    },
    getScatter: function () {
        this.setData({
            empty: [],
            use: []
        })
        var that = this
        wx.request({
            url: "http://localhost:8088/api/scatter",
            header: {
                'Content-Type': 'application/json'
            },
            success: function (result) {
                console.log(result.data, result.data.length)
                var use = [], empty = []
                for (var i = 0; i < result.data.length; i++) {
                    var next = [[result.data[i].x, result.data[i].y, result.data[i].id]]
                    console.log(next)
                    if (result.data[i].use) {
                        // use.push([result.data[i].x, result.data[i].y, i + 1]);
                        use = use.concat(next)
                    } else {
                        // empty.push([result.data[i].x, result.data[i].y, i + 1]);
                        empty = empty.concat(next)
                    }
                }
                that.setData({
                    use: use,
                    empty: empty
                })
                console.log("use", use, "empty", empty, "result", result);
                console.log("echart init")
                that.init()
            }, fail(res) {
                console.log(res)
            }
        })
        wx.request({
            url: "http://localhost:8088/api/scatter/count",
            header: {
                'Content-Type': 'application/json'
            },
            success: function (result) {
                console.log('parkCount',result.data)
                that.setData({
                    parkCount: result.data
                })
            }, fail(res) {
                console.log(res)
            }
        })
    },
    init: function () {
        // this.getScatter()
        this.ecComponent.init((canvas, width, height, dpr) => {
            // 获取组件的 canvas、width、height 后的回调函数
            // 在这里初始化图表
            const chart = echarts.init(canvas, null, {
                width: width,
                height: height,
                devicePixelRatio: dpr // new
            });
            setOption(chart, this.data.empty, this.data.use);

            // 将图表实例绑定到 this 上，可以在其他成员函数（如 dispose）中访问
            this.chart = chart;

            this.setData({
                isLoaded: true,
                isDisposed: false
            });

            // 注意这里一定要返回 chart 实例，否则会影响事件处理等
            return chart;
        });
    },
    onLoad: function () {
        console.log("onLoad")
        // this.getScatter()
    },
    onShow: function () {
        console.log("onShow")
        this.getScatter()
        var that = this
        interval = setInterval(
            function () {
                that.getScatter()
            }, 1000
        )
        // this.init()
    },
    dispose: function () {
        if (this.chart) {
            this.chart.dispose();
        }

        this.setData({
            isDisposed: true
        });
    },

    onHide: function () {
        clearInterval(interval);
    }

})
