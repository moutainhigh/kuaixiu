webpackJsonp([1], {
    FYxk: function (t, e) {
    }, NHnr: function (t, e, i) {
        "use strict";
        Object.defineProperty(e, "__esModule", {value: !0});
        var o = i("7+uW"), n = {
            name: "SIdentify",
            props: {
                identifyCode: {type: String, default: "1234"},
                fontSizeMin: {type: Number, default: 25},
                fontSizeMax: {type: Number, default: 30},
                backgroundColorMin: {type: Number, default: 220},
                backgroundColorMax: {type: Number, default: 255},
                dotColorMin: {type: Number, default: 20},
                dotColorMax: {type: Number, default: 40},
                contentWidth: {type: Number, default: 60},
                contentHeight: {type: Number, default: 32}
            },
            methods: {
                randomNum: function (t, e) {
                    return Math.floor(Math.random() * (e - t) + t)
                }, randomColor: function (t, e) {
                    return "rgb(" + this.randomNum(t, e) + "," + this.randomNum(t, e) + "," + this.randomNum(t, e) + ")"
                }, drawPic: function () {
                    var t = document.getElementById("s-canvas").getContext("2d");
                    t.textBaseline = "bottom", t.fillStyle = this.randomColor(this.backgroundColorMin, this.backgroundColorMax), t.fillRect(0, 0, this.contentWidth, this.contentHeight);
                    for (var e = 0; e < this.identifyCode.length; e++) this.drawText(t, this.identifyCode[e], e);
                    this.drawLine(t), this.drawDot(t)
                }, drawText: function (t, e, i) {
                    t.fillStyle = this.randomColor(50, 160), t.font = this.randomNum(this.fontSizeMin, this.fontSizeMax) + "px SimHei";
                    var o = (i + 1) * (this.contentWidth / (this.identifyCode.length + 1.5)),
                        n = this.randomNum(this.fontSizeMax, this.contentHeight - 5), a = this.randomNum(-30, 30);
                    t.translate(o, n), t.rotate(a * Math.PI / 180), t.fillText(e, 0, 0), t.rotate(-a * Math.PI / 180), t.translate(-o, -n)
                }, drawLine: function (t) {
                    for (var e = 0; e < 4; e++) t.strokeStyle = this.randomColor(100, 200), t.beginPath(), t.moveTo(this.randomNum(0, this.contentWidth), this.randomNum(0, this.contentHeight)), t.lineTo(this.randomNum(0, this.contentWidth), this.randomNum(0, this.contentHeight)), t.stroke()
                }, drawDot: function (t) {
                    for (var e = 0; e < 30; e++) t.fillStyle = this.randomColor(0, 255), t.beginPath(), t.arc(this.randomNum(0, this.contentWidth), this.randomNum(0, this.contentHeight), 1, 0, 2 * Math.PI), t.fill()
                }
            },
            watch: {
                identifyCode: function () {
                    this.drawPic()
                }
            },
            mounted: function () {
                this.drawPic()
            }
        }, a = {
            render: function () {
                var t = this.$createElement, e = this._self._c || t;
                return e("div", {staticClass: "s-canvas"}, [e("canvas", {
                    attrs: {
                        id: "s-canvas",
                        width: this.contentWidth,
                        height: this.contentHeight
                    }
                })])
            }, staticRenderFns: []
        }, s = {
            name: "recovery",
            components: {Sidentify: i("VU/8")(n, a, !1, null, null, null).exports},
            data: function () {
                return {
                    baseurl: "",
                    headImage: "",
                    centerImage: "",
                    isget: !1,
                    activityRole: [],
                    popupVisible: !1,
                    hd: "",
                    fm: "",
                    isdisable: !1,
                    title: "",
                    iphoneValue: "",
                    islogin: !1,
                    code: "",
                    imgCode: "",
                    currentTime: 60,
                    time: "获取短信验证码",
                    identifyCode: "",
                    identifyCodes: "1234567890QWERTYUIOPLKJHGFDSAZCXVBNMqwertyuioplkjhgfdszaxcvbnm"
                }
            },
            watch: {
                iphoneValue: function (t, e) {
                    var i = /[^0-9]/g;
                    t.indexOf(" ") > -1 && (this.iphoneValue = t.replace(/\s+/g, "")), i.test(t) && (this.iphoneValue = t.replace(i, ""))
                }
            },
            methods: {
                getcodevalue: function () {
                }, mobileInput: function () {
                }, onReady: function () {
                    this.identifyCode = "", this.makeCode(this.identifyCodes, 4)
                    console.log("初始化界面");
                }, getdialog: function () {
                    if ("立即使用" == this.title && (window.location.href = "https://m-super.com/ty_wap/product.html?bid=1&fm=" + this.fm + "&phone=" + localStorage.getItem("login_phone")), this.isget) return !1;
                    this.islogin ? this.getreceiveActivityCoupon() : (this.code = "", this.iphoneValue = "", this.popupVisible = !this.popupVisible)
                }, receive: function () {
                    var t = this;
                    if (this.iphoneValue && this.iphoneValue.trim()) if (/^(?:13\d|14\d|15\d|17\d|18\d|19\d)\d{5}(\d{3}|\*{3})$/.test(this.iphoneValue)) if (this.code && null != this.code) {
                        var e = {params: {phone: this.iphoneValue, checkCode: this.code}};
                        this.$http({
                            url: this.baseurl + "/recycle/checkLogin.do",
                            method: "post",
                            params: e
                        }).then(function (e) {
                            e.data.success ? 0 == e.data.resultCode && (localStorage.setItem("login_phone", t.iphoneValue), t.getreceiveActivityCoupon()) : 3 == e.data.resultCode && t.$toast({
                                message: "短信验证码错误",
                                position: "middle",
                                duration: 2e3
                            })
                        })
                    } else this.$toast({
                        message: "请输入短信验证码",
                        position: "middle",
                        duration: 2e3
                    }); else this.$toast({
                        message: "手机号码不对",
                        position: "middle",
                        duration: 2e3
                    }); else this.$toast({message: "请输入手机号码", position: "middle", duration: 2e3})
                }, goPCenter: function () {
                    window.location.href = "https://m-super.com/ty_wap/discountList.html?fm=" + this.fm + "&phone=" + localStorage.getItem("login_phone")
                }, getreceiveActivityCoupon: function () {
                    var t = this,
                        e = {params: {phone: localStorage.getItem("login_phone"), fm: this.fm, activityLabel: this.hd}};
                    this.$http({
                        url: this.baseurl + "/recycle/receiveActivityCoupon.do",
                        method: "post",
                        params: e
                    }).then(function (e) {
                        e.data.success ? 0 == e.data.resultCode ? (t.$toast({
                            message: "领取成功！",
                            position: "middle",
                            duration: 2e3
                        }), t.popupVisible = !1, t.islogin = !0, t.isget = !0, t.title = "立即使用") : 3 == e.data.resultCode && (t.title = "立即使用", t.isget = !0) : 3 == e.data.resultCode && (t.$toast({
                            message: e.data.resultMessage,
                            position: "middle",
                            duration: 2e3
                        }), t.title = "立即使用", t.isget = !0, t.popupVisible = !1)
                    })
                }, getCode: function () {
                    var t = this;
                    if (this.isdisable) return this.$toast({message: "已发送验证码", position: "middle", duration: 2e3}), !1;
                    if (this.iphoneValue && this.iphoneValue.trim()) if (/^1[34578]\d{9}$/.test(this.iphoneValue)) {
                        this.isdisable = !0;
                        var e = this.currentTime;
                        this.time = e + "秒后重发";
                        var i = setInterval(function () {
                            t.time = e - 1 + "秒后重发", --e <= 0 && (t.isdisable = !1, clearInterval(i), t.time = "重新获取", t.currentTime = 60)
                        }, 1e3);
                        this.textCode()
                    } else this.$toast({
                        message: "手机号码不对",
                        position: "middle",
                        duration: 2e3
                    }); else this.$toast({message: "请输入手机号码", position: "middle", duration: 2e3})
                }, getisLogin: function () {
                    localStorage.getItem("login_phone") ? (this.islogin = !0, this.title = "立即领取") : (this.islogin = !1, this.title = "立即领取")
                }, textCode: function () {
                    var t = {params: {phone: this.iphoneValue}};
                    this.$http({
                        url: this.baseurl + "/recycle/sendSmsCode.do",
                        method: "post",
                        params: t
                    }).then(function (t) {
                    })
                }, getcouponIndex: function () {
                    var t = this, e = {params: {fm: this.fm, label: this.hd}};
                    this.$http({
                        url: this.baseurl + "/recycle/getCouponIndex.do",
                        method: "post",
                        params: e
                    }).then(function (e) {
                        "0" == e.data.resultCode && (document.getElementsByClassName("content")[0].style.backgroundColor = "#" + e.data.result.centerImage.centercolorValue, document.getElementsByClassName("main")[0].style.backgroundColor = "#" + e.data.result.centerImage.centercolorValue, document.getElementsByClassName("heard")[0].style.backgroundColor = "#" + e.data.result.centerImage.centercolorValue, t.headImage = e.data.result.headImage.headUrl, t.centerImage = e.data.result.centerImage.centerUrl, t.activityRole = e.data.result.activityRole)
                    })
                }, makeCode: function (t, e) {
                    for (var i = 0; i < e; i++) this.identifyCode += this.identifyCodes[this.randomNum(0, this.identifyCodes.length)]
                }, randomNum: function (t, e) {
                    return Math.floor(Math.random() * (e - t) + t)
                }
            },
            created: function () {
                console.log("初始化界面1");
                var phoneNumber='15356152346';
                var loginPhone = eCacheUtil.storage.getCache(CacheKey.loginPhone);
                var HappyGoMobile = eCacheUtil.storage.getCache(CacheKey.HappyGoMobile);
                if(loginPhone || HappyGoMobile) {
                    if (loginPhone) {
                        phoneNumber = loginPhone
                    } else if (HappyGoMobile) {
                        phoneNumber = HappyGoMobile
                    }
                }
                if(phoneNumber!=''){
                    //通过单点登录的存在号码
                    console.log("存在号码");
                    this.hd = this.$route.query.hd;
                    this.fm = this.$route.query.fm;
                    var t = this,
                        e = {params: {phone: phoneNumber, fm: this.fm, activityLabel: this.hd}};
                    this.$http({
                        url: this.baseurl + "/recycle/receiveActivityCoupon.do",
                        method: "post",
                        params: e
                    }).then(function (e) {
                        console.log("领取："+e.data.resultCode);
                        console.log("领取："+e.data.resultMessage);
                        e.data.success ? 0 == e.data.resultCode ? (t.$toast({
                            message: "领取成功！",
                            position: "middle",
                            duration: 2e3
                        }), t.popupVisible = !1, t.islogin = !0) : 3 == e.data.resultCode  : 3 == e.data.resultCode && (t.$toast({
                            message: e.data.resultMessage,
                            position: "middle",
                            duration: 2e3
                        }), t.title = "立即使用", t.isget = !0, t.popupVisible = !1)
                    })

                }else{
                    console.log("不存在号码");
                }


                this.baseurl = window.location.protocol + "//m-super.com", this.hd = this.$route.query.hd, this.fm = this.$route.query.fm;
                var t = this.$route.query.phone;
                t && localStorage.setItem("login_phone", t), this.getisLogin()
            },
            updated: function () {
            },
            mounted: function () {
                this.getcouponIndex(), this.identifyCode = "", this.makeCode(this.identifyCodes, 4)
            }
        }, r = {
            render: function () {
                var t = this, e = t.$createElement, i = t._self._c || e;
                return i("div", {staticClass: "content"}, [i("div", {staticClass: "heard"}, [i("div", {staticClass: "bg"}, [i("img", {
                    staticStyle: {width: "100%"},
                    attrs: {src: t.headImage, alt: ""}
                })]), t._v(" "), t._m(0)]), t._v(" "), i("div", {staticClass: "main"}, [i("div", {staticClass: "coupon"}, [i("div", [i("img", {
                    staticStyle: {width: "100%"},
                    attrs: {src: t.centerImage, alt: ""}
                })]), t._v(" "), i("div", {
                    staticStyle: {
                        "text-align": "center",
                        "margin-top": "28px",
                        "margin-bottom": "12px"
                    }
                }, [i("button", {
                    class: t.isget ? "disprimary" : "primary",
                    on: {click: t.getdialog}
                }, [t._v(t._s(t.title))])]), t._v(" "), i("div", {
                    staticStyle: {
                        "text-align": "center",
                        "font-size": "14px",
                        color: "#fff"
                    }, on: {click: t.goPCenter}
                }, ["立即使用" == t.title ? i("span", [t._v("\n            查看红包>\n          ")]) : t._e()])]), t._v(" "), i("div", {staticClass: "rule"}, [t._m(1), t._v(" "), t._l(t.activityRole, function (e, o) {
                    return i("div", {
                        key: e.id,
                        staticStyle: {color: "#fff", "font-size": "14px", margin: "20px 0"}
                    }, [t._v("\n          " + t._s(o + 1) + "、" + t._s(e.role) + "\n        ")])
                })], 2)]), t._v(" "), i("mt-popup", {
                    attrs: {position: "popup-fade"},
                    model: {
                        value: t.popupVisible, callback: function (e) {
                            t.popupVisible = e
                        }, expression: "popupVisible"
                    }
                }, [i("div", [i("div", {staticClass: "tips"}, [t._v("\n  温馨提示"), i("span", {
                    staticClass: "iconfont icon-guanbi iconstyleP",
                    on: {
                        click: function (e) {
                            t.popupVisible = !1
                        }
                    }
                })]), t._v(" "), i("div", {staticClass: "bod"}, [i("div", {
                    staticStyle: {
                        border: "1px solid #dddddd",
                        height: "32px"
                    }
                }, [i("input", {
                    directives: [{
                        name: "model",
                        rawName: "v-model.trim",
                        value: t.iphoneValue,
                        expression: "iphoneValue",
                        modifiers: {trim: !0}
                    }],
                    staticStyle: {"margin-top": "9px", "font-size": "14px", "text-indent": "8px"},
                    attrs: {placeholder: "请输入你的手机号码", name: "phone", maxlength: "11", type: "tel"},
                    domProps: {value: t.iphoneValue},
                    on: {
                        input: [function (e) {
                            e.target.composing || (t.iphoneValue = e.target.value.trim())
                        }, t.mobileInput], blur: function (e) {
                            t.$forceUpdate()
                        }
                    }
                })]), t._v(" "), i("div", [i("div", {
                    staticStyle: {
                        border: "1px solid #dddddd",
                        height: "32px",
                        "margin-top": "12px"
                    }
                }, [i("input", {
                    directives: [{name: "model", rawName: "v-model", value: t.code, expression: "code"}],
                    staticStyle: {
                        "margin-top": "6px",
                        "font-size": "14px",
                        display: "inline-block",
                        "text-indent": "8px",
                        width: "59%"
                    },
                    attrs: {name: "phone", type: "number", maxlength: "6"},
                    domProps: {value: t.code},
                    on: {
                        input: [function (e) {
                            e.target.composing || (t.code = e.target.value)
                        }, t.getcodevalue]
                    }
                }), t._v(" "), i("div", {
                    class: t.isdisable ? "bgcolor" : "",
                    staticStyle: {
                        display: "inline-block",
                        "font-size": "10px",
                        padding: "0 10px",
                        height: "32px",
                        float: "right",
                        "border-left": "1px solid #dddddd",
                        "line-height": "32px",
                        color: "#999999"
                    },
                    on: {click: t.getCode}
                }, [t._v(t._s(t.time))])])]), t._v(" "), i("div", [i("div", {
                    staticClass: "dia",
                    staticStyle: {"text-align": "center", margin: "12px 0 4px"}
                }, [i("button", {on: {click: t.receive}}, [t._v("立即领取")])])])])])])], 1)
            }, staticRenderFns: [function () {
                var t = this.$createElement, e = this._self._c || t;
                return e("div", {staticClass: "but"}, [e("div", [this._v("用劵下单价更高")])])
            }, function () {
                var t = this.$createElement, e = this._self._c || t;
                return e("div", {
                    staticStyle: {
                        "text-align": "center",
                        display: "flex",
                        "margin-bottom": "28px"
                    }
                }, [e("span", {
                    staticClass: "linel flex-item",
                    staticStyle: {position: "relative", width: "34%"}
                }), this._v(" "), e("span", {
                    staticClass: "flex-item",
                    staticStyle: {
                        color: "#fff",
                        margin: "0 14px",
                        width: "36%",
                        "line-height": "4px",
                        "font-size": "17px"
                    }
                }, [this._v("活动规则")]), this._v(" "), e("span", {
                    staticClass: "liner flex-item",
                    staticStyle: {position: "relative", width: "34%"}
                })])
            }]
        };
        var l = i("VU/8")(s, r, !1, function (t) {
            i("FYxk")
        }, null, null).exports, d = i("/ocq"), h = {
            render: function () {
                var t = this, e = t.$createElement, i = t._self._c || e;
                return i("div", {staticClass: "hello"}, [i("h1", [t._v(t._s(t.msg))]), t._v(" "), i("h2", [t._v("Essential Links")]), t._v(" "), t._m(0), t._v(" "), i("h2", [t._v("Ecosystem")]), t._v(" "), t._m(1)])
            }, staticRenderFns: [function () {
                var t = this, e = t.$createElement, i = t._self._c || e;
                return i("ul", [i("li", [i("a", {
                    attrs: {
                        href: "https://vuejs.org",
                        target: "_blank"
                    }
                }, [t._v("\n        Core Docs\n      ")])]), t._v(" "), i("li", [i("a", {
                    attrs: {
                        href: "https://forum.vuejs.org",
                        target: "_blank"
                    }
                }, [t._v("\n        Forum\n      ")])]), t._v(" "), i("li", [i("a", {
                    attrs: {
                        href: "https://chat.vuejs.org",
                        target: "_blank"
                    }
                }, [t._v("\n        Community Chat\n      ")])]), t._v(" "), i("li", [i("a", {
                    attrs: {
                        href: "https://twitter.com/vuejs",
                        target: "_blank"
                    }
                }, [t._v("\n        Twitter\n      ")])]), t._v(" "), i("br"), t._v(" "), i("li", [i("a", {
                    attrs: {
                        href: "http://vuejs-templates.github.io/webpack/",
                        target: "_blank"
                    }
                }, [t._v("\n        Docs for This Template\n      ")])])])
            }, function () {
                var t = this.$createElement, e = this._self._c || t;
                return e("ul", [e("li", [e("a", {
                    attrs: {
                        href: "http://router.vuejs.org/",
                        target: "_blank"
                    }
                }, [this._v("\n        vue-router\n      ")])]), this._v(" "), e("li", [e("a", {
                    attrs: {
                        href: "http://vuex.vuejs.org/",
                        target: "_blank"
                    }
                }, [this._v("\n        vuex\n      ")])]), this._v(" "), e("li", [e("a", {
                    attrs: {
                        href: "http://vue-loader.vuejs.org/",
                        target: "_blank"
                    }
                }, [this._v("\n        vue-loader\n      ")])]), this._v(" "), e("li", [e("a", {
                    attrs: {
                        href: "https://github.com/vuejs/awesome-vue",
                        target: "_blank"
                    }
                }, [this._v("\n        awesome-vue\n      ")])])])
            }]
        };
        var u = i("VU/8")({
            name: "HelloWorld", data: function () {
                return {msg: "Welcome to Your Vue.js App"}
            }
        }, h, !1, function (t) {
            i("Ugm9")
        }, "data-v-694cd902", null).exports;
        o.default.use(d.a);
        var c = new d.a({routes: [{path: "/", name: "HelloWorld", component: u}]}), p = i("Au9i"), m = i.n(p),
            f = (i("d8/S"), i("mtWM")), g = i.n(f);
        g.a.defaults.headers.post["Content-Type"] = "application/json", o.default.use(m.a), o.default.config.productionTip = !1, o.default.$toast = o.default.prototype.$toast = p.Toast, o.default.component(p.Header.name, p.Header), o.default.component(p.Popup.name, p.Popup), o.default.component(p.Button.name, p.Button), o.default.component(p.Picker.name, p.Picker), o.default.prototype.$http = g.a, new o.default({
            el: "#app",
            router: c,
            components: {App: l},
            template: "<App/>"
        })
    }, Ugm9: function (t, e) {
    }, "d8/S": function (t, e) {
    }
}, ["NHnr"]);
