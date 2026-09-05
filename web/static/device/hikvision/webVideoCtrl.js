!function () {
    if (!window.WebVideoCtrl) {
        var e = function () {
            function e(e, t, n) {
                return (e << 16 | t << 8 | n).toString(16)
            }

            function n(e, t) {
                return (Array(t).join("0") + e).slice(-t)
            }

            function r(e) {
                var t = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
                if (e && t.test(e)) {
                    if (4 === e.length) {
                        for (var n = "#", r = 1; 4 > r; r += 1) n += e.slice(r, r + 1).concat(e.slice(r, r + 1));
                        e = n
                    }
                    for (var i = [], r = 1; 7 > r; r += 2) i.push(parseInt("0x" + e.slice(r, r + 2)));
                    return "[" + i.join(",") + ",0.6]"
                }
                return e
            }

            function o() {
                return void 0 !== o.unique ? o.unique : (this.szIP = "", this.szHostName = "", this.szAuth = "", this.szHttpProtocol = "http://", this.iCGIPort = 80, this.szDeviceIdentify = "", this.iDevicePort = -1, this.iHttpPort = -1, this.iHttpsPort = -1, this.iRtspPort = -1, this.iWSPort = -1, this.iAudioType = 1, this.m_iAudioBitRate = -1, this.m_iAudioSamplingRate = -1, this.iDeviceProtocol = b, this.oProtocolInc = null, this.iAnalogChannelNum = 0, this.szDeviceType = "", this.bVoiceTalk = !1, this.oAuthType = {}, this.oStreamCapa = {
                    bObtained: !1,
                    bSupportShttpPlay: !1,
                    bSupportShttpPlayback: !1,
                    bSupportShttpsPlay: !1,
                    bSupportShttpsPlayback: !1,
                    bSupportShttpPlaybackTransCode: !1,
                    bSupportShttpsPlaybackTransCode: !1,
                    iIpChanBase: 1
                }, o.unique = this, void 0)
            }

            function s() {
                this.id = this.createUUID()
            }

            var a = "100%", u = "100%", l = "", c = "";
            oSecurityCap = {}, szAESKey = "";
            var d = {
                szversion: "websdk3.220200429",
                szContainerID: "",
                szColorProperty: "",
                szOcxClassId: "clsid:FDF0038A-CF64-4634-81AB-80F0A7946D6C",
                szMimeTypes: "application/webvideo-plugin-kit",
                szBasePath: "",
                iWndowType: 1,
                iPlayMode: 2,
                bWndFull: !0,
                iPackageType: 2,
                bDebugMode: !1,
                bNoPlugin: !0,
                cbSelWnd: null,
                cbDoubleClickWnd: null,
                cbEvent: null,
                cbRemoteConfig: null,
                cbInitPluginComplete: null,
                proxyAddress: null
            }, f = null, h = 0, p = !1, m = [], y = [], v = null, g = null, P = null, S = null, I = this, C = null;
            this.w_options = d, this.w_deviceSet = m, this.w_wndSet = y, this.w_xmlLocalCfg = C;
            var b = 1, x = 2, w = 200, T = 0, _ = 1, D = 2, k = 3, M = 4, z = 5, R = 6, A = 0, L = 2, E = 3, q = 21,
                F = -1, B = 0, W = "IPCamera", H = "IPDome", N = "IPZoom",
                G = "<?xml version='1.0' encoding='utf-8'?><FileVersion><Platform name='win32'><npWebVideoKitPlugin.dll>3,0,6,2</npWebVideoKitPlugin.dll><WebVideoKitActiveX.ocx>3,0,6,2</WebVideoKitActiveX.ocx><PlayCtrl.dll>7,3,3,61</PlayCtrl.dll><StreamTransClient.dll>1,1,3,6</StreamTransClient.dll><SystemTransform.dll>2,5,2,8</SystemTransform.dll><NetStream.dll>1,0,5,59</NetStream.dll></Platform></FileVersion>";
            window.GetSelectWndInfo = function (e) {
                if (lt()) {
                    h = e;
                    var n = [];
                    n.push("<RealPlayInfo>"), n.push("<SelectWnd>" + e + "</SelectWnd>"), n.push("</RealPlayInfo>"), d.cbSelWnd && d.cbSelWnd(S.loadXML(n.join("")))
                } else {
                    var r = S.loadXML(e);
                    if (t.$XML(r).find("SelectWnd", !0).length > 0) {
                        h = parseInt(t.$XML(r).find("SelectWnd").eq(0).text(), 10), null === C && K();
                        var n = [];
                        n.push("<RealPlayInfo>"), n.push("<SelectWnd>" + h + "</SelectWnd>"), n.push("</RealPlayInfo>"), d.cbSelWnd && d.cbSelWnd(S.loadXML(n.join("")))
                    } else if (t.$XML(r).find("DoubleClickWnd", !0).length > 0) {
                        var i = parseInt(t.$XML(r).find("DoubleClickWnd").eq(0).text(), 10);
                        p = "0" === t.$XML(r).find("IsFullScreen").eq(0).text(), d.cbDoubleClickWnd && d.cbDoubleClickWnd(i, p)
                    }
                }
            }, window.WindowDblClick = function (e) {
                p = e, d.cbDoubleClickWnd && d.cbDoubleClickWnd(h, p)
            }, window.ZoomInfoCallback = function (e) {
                var t = I.findWndIndexByIndex(h);
                if (-1 != t) {
                    var n = y[t];
                    if (t = I.findDeviceIndexByIP(n.szDeviceIdentify), -1 != t) {
                        var r = m[t];
                        r.oProtocolInc.set3DZoom(r, n, e, {
                            success: function () {
                            }, error: function () {
                            }
                        })
                    }
                }
            }, window.PluginEventHandler = function (e, t, n) {
                lt() ? d.cbEvent && d.cbEvent(e, t, n) : (A == t || L == t ? I.I_Stop(e) : q == t ? I.I_StopRecord(e) : E == t && I.I_StopVoiceTalk(), d.cbEvent && d.cbEvent(t, e, n))
            }, window.GetHttpInfo = function (e, t) {
                mt.prototype.processCallback(e, t)
            }, window.RemoteConfigInfo = function (e) {
                d.cbRemoteConfig && d.cbRemoteConfig(e)
            }, window.KeyBoardEventInfo = function (e) {
                100 === parseInt(e, 10) && (p = !1, d.cbDoubleClickWnd && d.cbDoubleClickWnd(h, p))
            };
            var O = function () {
                if (d.bDebugMode) {
                    var e = X(arguments);
                    v._alert(e)
                }
            }, X = function () {
                for (var e = arguments[0], t = 1; t < arguments.length; t++) e = e.replace("%s", arguments[t]);
                return e
            }, U = function (e) {
                var t = e.indexOf(":");
                return t > -1 ? e.substring(0, t) : e
            }, j = function (e) {
                return "undefined" == typeof e
            }, V = function (e) {
                return "[object Object]" === Object.prototype.toString.call(e)
            }, Z = function (e, t) {
                var n = "", r = {
                    type: "GET", async: !1, success: function (e) {
                        e && e.Token && (n = e.Token.value)
                    }
                }, i = "ISAPI/Security/token?format=json";
                return I.I_SendHTTPRequest(t, i, r), n
            }, J = function (e, n, r) {
                I.I_GetSecurityVersion(e, {
                    success: function (e) {
                        oSecurityCap.iKeyIterateNum = parseInt(t.$XML(e).find("keyIterateNum").eq(0).text(), 10), oSecurityCap.oIrreversibleEncrypt = {
                            bSupport: "true" === t.$XML(e).find("isIrreversible").eq(0).text(),
                            salt: t.$XML(e).find("salt").eq(0).text()
                        }, szAESKey = S.strToAESKey(n, r)
                    }
                })
            }, Y = function () {
                var e = "";
                if (S.browser().msie) e = "<object classid='" + d.szOcxClassId + "' codebase='' standby='Waiting...' " + "id='" + l + "' width='" + a + "' height='" + u + "' align='center' >" + "<param name='wndtype' value='" + d.iWndowType + "'>" + "<param name='playmode' value='" + d.iPlayMode + "'>" + "<param name='colors' value='" + d.szColorProperty + "'></object>"; else for (var t = navigator.mimeTypes.length, n = 0; t > n; n++) navigator.mimeTypes[n].type.toLowerCase() == d.szMimeTypes && (e = "<embed align='center' type='" + d.szMimeTypes + "' width='" + a + "' height='" + u + "' name='" + c + "' wndtype='" + d.iWndowType + "' playmode='" + d.iPlayMode + "' colors='" + d.szColorProperty + "'>");
                return e
            }, K = function () {
                if (!lt() && null !== f) {
                    var e = f.HWP_GetLocalConfig();
                    C = S.loadXML(e)
                }
            }, Q = function (e) {
                I.I_GetDeviceInfo(e.szIP, {
                    success: function (n) {
                        e.szDeviceType = t.$XML(n).find("deviceType").eq(0).text()
                    }
                }), I.I_GetAnalogChannelInfo(e.szIP, {
                    success: function () {
                    }, error: function () {
                    }
                }), I.I_GetAudioInfo(e.szIP, {
                    success: function (n) {
                        var r = t.$XML(n).find("audioCompressionType", !0);
                        if (r.length > 0) {
                            var i = t.$XML(r).eq(0).text(), o = 0;
                            "G.711ulaw" == i ? o = 1 : "G.711alaw" == i ? o = 2 : "G.726" == i ? o = 3 : "MP2L2" == i || "MPEL2" == i ? o = 4 : "G.722.1" == i ? o = 0 : "AAC" == i ? o = 5 : "PCM" == i && (o = 6), e.iAudioType = o
                        }
                        e.m_iAudioBitRate = "" !== t.$XML(n).find("audioBitRate").eq(0).text() ? 1e3 * parseInt(t.$XML(n).find("audioBitRate").eq(0).text(), 10) : 0, e.m_iAudioSamplingRate = "" !== t.$XML(n).find("audioSamplingRate").eq(0).text() ? 1e3 * parseInt(t.$XML(n).find("audioSamplingRate").eq(0).text(), 10) : 0
                    }
                })
            }, et = function (e) {
                e.bSupportWebsocket = !1, e.bSupportSubStreamPlayback = !1, e.oProtocolInc.getSystemCapa(e, {
                    success: function (n) {
                        var r = t.$XML(n).find("NetworkCap").eq(0).find("isSupportWebsocket", !0);
                        r.length > 0 && (e.bSupportWebsocket = "true" === t.$XML(n).find("NetworkCap").eq(0).find("isSupportWebsocket").eq(0).text()), e.bSupportWebsocket = !0, r = t.$XML(n).find("RacmCap").eq(0).find("isSupportMainAndSubRecord", !0), r.length > 0 && (e.bSupportSubStreamPlayback = "true" === t.$XML(n).find("RacmCap").eq(0).find("isSupportMainAndSubRecord").eq(0).text()), e.bSupportSubStreamPlayback = !0
                    }
                })
            }, tt = function () {
                var e = d.bWndFull ? 1 : 0;
                f.HWP_SetCanFullScreen(e), f.HWP_SetPackageType(d.iPackageType)
            }, nt = function (e) {
                var t = -1, n = -1, r = -1, i = null;
                if (st(e)) i = rt(e), t = i.iRtspPort, r = i.iDevicePort; else {
                    for (var o = ot(e), s = !1, a = 0; a < o.length; a++) if (o[a].ipv4 == e.szIP || o[a].ipv6 == e.szIP) {
                        s = !0;
                        break
                    }
                    s ? i = rt(e) : (i = it(e), -1 == i.iRtspPort && -1 == i.iDevicePort && (i = rt(e))), t = i.iRtspPort, n = i.iHttpPort, r = i.iDevicePort
                }
                return i
            }, rt = function (e) {
                var n = -1, r = -1, i = -1;
                return e.oProtocolInc.getPortInfo(e, {
                    async: !1, success: function (e) {
                        var o = t.$XML(e).find("AdminAccessProtocol", !0);
                        n = 554;
                        for (var s = 0, a = o.length; a > s; s++) "rtsp" === t.$XML(o).eq(s).find("protocol").eq(0).text().toLowerCase() && (n = parseInt(t.$XML(o).eq(s).find("portNo").eq(0).text(), 10)), "http" === t.$XML(o).eq(s).find("protocol").eq(0).text().toLowerCase() && (r = parseInt(t.$XML(o).eq(s).find("portNo").eq(0).text(), 10)), "dev_manage" === t.$XML(o).eq(s).find("protocol").eq(0).text().toLowerCase() && (i = parseInt(t.$XML(o).eq(s).find("portNo").eq(0).text(), 10))
                    }, error: function () {
                        n = -1, r = -1, i = -1
                    }
                }), {iRtspPort: n, iHttpPort: r, iDevicePort: i}
            }, it = function (e) {
                var n = -1, r = -1, i = -1;
                return e.oProtocolInc.getUPnPPortStatus(e, {
                    async: !1, success: function (e) {
                        for (var o = t.$XML(e).find("portStatus", !0), s = 0, a = o.length; a > s; s++) "rtsp" == t.$XML(o).eq(s).find("internalPort").eq(0).text().toLowerCase() && (n = parseInt(t.$XML(o).eq(s).find("externalPort").eq(0).text(), 10)), "http" == t.$XML(o).eq(s).find("internalPort").eq(0).text().toLowerCase() && (r = parseInt(t.$XML(o).eq(s).find("externalPort").eq(0).text(), 10)), "admin" == t.$XML(o).eq(s).find("internalPort").eq(0).text().toLowerCase() && (i = parseInt(t.$XML(o).eq(s).find("externalPort").eq(0).text(), 10))
                    }, error: function () {
                        n = -1, r = -1, i = -1
                    }
                }), {iRtspPort: n, iHttpPort: r, iDevicePort: i}
            }, ot = function (e) {
                var n = [];
                return e.oProtocolInc.getNetworkBond(e, {
                    async: !1, success: function (r) {
                        "true" == t.$XML(r).find("enabled").eq(0).text() ? n.push({
                            ipv4: t.$XML(r).find("ipAddress").eq(0).text(),
                            ipv6: t.$XML(r).find("ipv6Address").eq(0).text()
                        }) : e.oProtocolInc.getNetworkInterface(e, {
                            async: !1, success: function (e) {
                                for (var r = t.$XML(e).find("NetworkInterface", !0), i = 0, o = r.length; o > i; i++) {
                                    n.push({
                                        ipv4: t.$XML(e).find("ipAddress").eq(0).text(),
                                        ipv6: t.$XML(e).find("ipv6Address").eq(0).text()
                                    });
                                    break
                                }
                            }, error: function () {
                            }
                        })
                    }, error: function () {
                        e.oProtocolInc.getNetworkInterface(e, {
                            async: !1, success: function (e) {
                                for (var r = t.$XML(e).find("NetworkInterface", !0), i = 0, o = r.length; o > i; i++) {
                                    n.push({
                                        ipv4: t.$XML(e).find("ipAddress").eq(0).text(),
                                        ipv6: t.$XML(e).find("ipv6Address").eq(0).text()
                                    });
                                    break
                                }
                            }, error: function () {
                            }
                        })
                    }
                }), n
            }, st = function (e) {
                var n = !1;
                return e.oProtocolInc.getPPPoEStatus(e, {
                    async: !1, success: function (e) {
                        n = t.$XML(e).find("ipAddress", !0).length > 0 ? !0 : t.$XML(e).find("ipv6Address", !0).length > 0 ? !0 : !1
                    }, error: function () {
                        n = !1
                    }
                }), n
            }, at = function (e) {
                e.oProtocolInc instanceof yt && e.oProtocolInc.getSDKCapa(e, {
                    async: !1, success: function (n) {
                        e.oStreamCapa.bObtained = !0, e.oStreamCapa.bSupportShttpPlay = "true" === t.$XML(n).find("isSupportHttpPlay").eq(0).text(), e.oStreamCapa.bSupportShttpPlayback = "true" === t.$XML(n).find("isSupportHttpPlayback").eq(0).text(), e.oStreamCapa.bSupportShttpsPlay = "true" === t.$XML(n).find("isSupportHttpsPlay").eq(0).text(), e.oStreamCapa.bSupportShttpsPlayback = "true" === t.$XML(n).find("isSupportHttpsPlayback").eq(0).text(), e.oStreamCapa.bSupportShttpPlaybackTransCode = "true" === t.$XML(n).find("isSupportHttpTransCodePlayback").eq(0).text(), e.oStreamCapa.bSupportShttpsPlaybackTransCode = "true" === t.$XML(n).find("isSupportHttpsTransCodePlayback").eq(0).text(), e.oStreamCapa.iIpChanBase = t.$XML(n).find("ipChanBase", !0).length > 0 ? parseInt(t.$XML(n).find("ipChanBase").eq(0).text(), 10) : 1
                    }, error: function () {
                        e.oStreamCapa.bObtained = !0
                    }
                })
            }, ut = function (e) {
                var t = {TransFrameRate: "", TransResolution: "", TransBitrate: ""};
                if (S.extend(t, e), "" == t.TransFrameRate || "" == t.TransResolution || "" == t.TransBitrate) return "";
                var n = [];
                return n.push("<?xml version='1.0' encoding='UTF-8'?>"), n.push("<CompressionInfo>"), n.push("<TransFrameRate>" + t.TransFrameRate + "</TransFrameRate>"), n.push("<TransResolution>" + t.TransResolution + "</TransResolution>"), n.push("<TransBitrate>" + t.TransBitrate + "</TransBitrate>"), n.push("</CompressionInfo>"), n.join("")
            }, lt = function () {
                if (d.bNoPlugin) {
                    var e = S.browser();
                    return e.chrome && parseInt(e.version, 10) > 45 || e.mozilla && parseInt(e.version, 10) > 52 ? !0 : !1
                }
                return !1
            }, ct = function (e) {
                var t = location.hostname, n = location.port || "80";
                return d.proxyAddress && (t = d.proxyAddress.ip, n = d.proxyAddress.port), /^(http|https):\/\/([^\/]+)(.+)$/.test(e) && (e = e.replace(RegExp.$2, t + ":" + n)), S.cookie("webVideoCtrlProxy", RegExp.$2, {raw: !0}), e
            }, dt = function (e) {
                var t = location.hostname, n = location.port || "80";
                return /^(ws):\/\/([^\/:]+):(\d+)\/(.+)$/.test(e) && (e = e.replace(RegExp.$2 + ":" + RegExp.$3, t + ":" + n)), e.indexOf("ws") > -1 ? S.cookie("webVideoCtrlProxyWs", RegExp.$2 + ":" + RegExp.$3, {raw: !0}) : S.cookie("webVideoCtrlProxyWss", RegExp.$2 + ":" + RegExp.$3, {raw: !0}), S.cookie("webVideoCtrlProxyWsChannel", RegExp.$4, {raw: !0}), e + "/webSocketVideoCtrlProxy"
            }, ft = function () {
                var e = "<ResponseStatus>";
                return e += "<requestURL></requestURL>", e += "<statusCode>4</statusCode>", e += "<statusString>Invalid Operation</statusString>", e += "<subStatusCode>notSupport</subStatusCode>", e += "</ResponseStatus>", S.loadXML(e)
            };
            this.I_SupportNoPlugin = function () {
                return lt()
            }, this.I_DestroyWorker = function () {
                null !== f && lt() && f.JS_DestroyWorker()
            }, this.I_Resize = function (e, t) {
                null !== f && lt() && (a = e, u = t, f.JS_Resize(e, t))
            }, this.I_InitPlugin = function (e, t, n) {
                if (a = e, u = t, S.extend(d, n), lt()) {
                    var r = S.getDirName();
                    r && ("object" == typeof exports && "undefined" != typeof module || ("function" == typeof define && define.amd ? require([r + "/jsPlugin-1.2.0.min.js"], function (e) {
                        window.JSPlugin = e.JSPlugin, n.cbInitPluginComplete && n.cbInitPluginComplete()
                    }) : S.loadScript(r + "/jsPlugin-1.2.0.min.js", function () {
                        n.cbInitPluginComplete && n.cbInitPluginComplete()
                    }))), j(document.fullScreen) ? j(document.webkitIsFullScreen) ? j(document.mozFullScreen) || document.addEventListener("mozfullscreenchange", function () {
                        var e = document.fullscreen || document.webkitIsFullScreen || document.mozFullScreen || !1;
                        p && !e && window.KeyBoardEventInfo(100)
                    }) : document.addEventListener("webkitfullscreenchange", function () {
                        var e = document.fullscreen || document.webkitIsFullScreen || document.mozFullScreen || !1;
                        p && !e && window.KeyBoardEventInfo(100)
                    }) : document.addEventListener("fullscreenchange", function () {
                        var e = document.fullscreen || document.webkitIsFullScreen || document.mozFullScreen || !1;
                        p && !e && window.KeyBoardEventInfo(100)
                    }), window.addEventListener("unload", function () {
                        null !== f && f.JS_DestroyWorker()
                    })
                } else n.cbInitPluginComplete && n.cbInitPluginComplete()
            }, this.I_InsertOBJECTPlugin = function (e) {
                if (j(e) || (d.szContainerID = e, a = $("#" + e).width(), u = $("#" + e).height()), null == document.getElementById(d.szContainerID)) return -1;
                if (null != document.getElementById(l) || 0 != document.getElementsByName(l).length) return -1;
                if (lt()) {
                    var t = {
                        szId: e,
                        iType: 1,
                        iWidth: a,
                        iHeight: u,
                        iMaxSplit: 4,
                        iCurrentSplit: d.iWndowType,
                        szBasePath: S.getDirName()
                    }, n = d.szColorProperty;
                    if ("" != n) {
                        for (var r = {}, i = n.split(";"), o = "", s = 0, h = i.length; h > s; s++) o = i[s], o.indexOf("sub-background") > -1 ? r.background = "#" + o.split(":")[1] : o.indexOf("sub-border-select") > -1 ? r.borderSelect = "#" + o.split(":")[1] : o.indexOf("sub-border") > -1 && (r.border = "#" + o.split(":")[1]);
                        t.oStyle = r
                    }
                    f = new JSPlugin(t)
                } else document.getElementById(d.szContainerID).innerHTML = Y(), f = S.browser().msie ? document.getElementById(l) : document.getElementsByName(c)[0];
                return null == f && null == f.object ? -1 : ("object" == typeof window.attachEvent && S.browser().msie && (f.attachEvent("GetSelectWndInfo", GetSelectWndInfo), f.attachEvent("ZoomInfoCallback", ZoomInfoCallback), f.attachEvent("GetHttpInfo", GetHttpInfo), f.attachEvent("PluginEventHandler", PluginEventHandler), f.attachEvent("RemoteConfigInfo", RemoteConfigInfo), f.attachEvent("KeyBoardEventInfo", KeyBoardEventInfo)), K(), 0)
            }, this.I_WriteOBJECT_XHTML = function () {
                return lt() ? -1 : (document.writeln(Y()), f = S.browser().msie ? document.getElementById(l) : document.getElementsByName(c)[0], null == f && null == f.object ? -1 : ("object" == typeof window.attachEvent && S.browser().msie && (f.attachEvent("GetSelectWndInfo", GetSelectWndInfo), f.attachEvent("ZoomInfoCallback", ZoomInfoCallback), f.attachEvent("GetHttpInfo", GetHttpInfo), f.attachEvent("PluginEventHandler", PluginEventHandler), f.attachEvent("RemoteConfigInfo", RemoteConfigInfo), f.attachEvent("KeyBoardEventInfo", KeyBoardEventInfo)), K(), 0))
            }, this.I_OpenFileDlg = function (e) {
                var t = "";
                if (lt()) return t;
                if (t = f.HWP_OpenFileBrowser(e, ""), null == t) return "";
                if (1 == e) {
                    if (t.length > 100) return -1
                } else if (t.length > 130) return -1;
                return t
            }, this.I2_OpenFileDlg = function (e) {
                var t = "", n = $.Deferred();
                return lt() ? f.JS_OpenFileBrowser(e, "").then(function (t) {
                    null != t ? 1 == e ? t.length > 100 && n.resolve(-1) : t.length > 130 && n.resolve(-1) : n.resolve(), n.resolve(t)
                }) : (t = f.HWP_OpenFileBrowser(e, ""), null != t ? 1 == e ? t.length > 100 && n.resolve(-1) : t.length > 130 && n.resolve(-1) : n.resolve(), n.resolve(t)), n
            }, this.I_GetLocalCfg = function () {
                var e = null;
                if (lt()) return e;
                var n = f.HWP_GetLocalConfig(), r = [];
                return C = S.loadXML(n), r.push("<LocalConfigInfo>"), r.push("<ProtocolType>" + t.$XML(C).find("ProtocolType").eq(0).text() + "</ProtocolType>"), r.push("<PackgeSize>" + t.$XML(C).find("PackgeSize").eq(0).text() + "</PackgeSize>"), r.push("<PlayWndType>" + t.$XML(C).find("PlayWndType").eq(0).text() + "</PlayWndType>"), r.push("<BuffNumberType>" + t.$XML(C).find("BuffNumberType").eq(0).text() + "</BuffNumberType>"), r.push("<RecordPath>" + t.$XML(C).find("RecordPath").eq(0).text() + "</RecordPath>"), r.push("<CapturePath>" + t.$XML(C).find("CapturePath").eq(0).text() + "</CapturePath>"), r.push("<PlaybackFilePath>" + t.$XML(C).find("PlaybackFilePath").eq(0).text() + "</PlaybackFilePath>"), r.push("<PlaybackPicPath>" + t.$XML(C).find("PlaybackPicPath").eq(0).text() + "</PlaybackPicPath>"), r.push("<DeviceCapturePath>" + t.$XML(C).find("DeviceCapturePath").eq(0).text() + "</DeviceCapturePath>"), r.push("<DownloadPath>" + t.$XML(C).find("DownloadPath").eq(0).text() + "</DownloadPath>"), r.push("<IVSMode>" + t.$XML(C).find("IVSMode").eq(0).text() + "</IVSMode>"), r.push("<CaptureFileFormat>" + t.$XML(C).find("CaptureFileFormat").eq(0).text() + "</CaptureFileFormat>"), r.push("</LocalConfigInfo>"), e = S.loadXML(r.join(""))
            }, this.I_SetLocalCfg = function (e) {
                if (lt()) return -1;
                var n = S.loadXML(e), r = !1;
                return t.$XML(C).find("ProtocolType").eq(0).text(t.$XML(n).find("ProtocolType").eq(0).text()), t.$XML(C).find("PackgeSize").eq(0).text(t.$XML(n).find("PackgeSize").eq(0).text()), t.$XML(C).find("PlayWndType").eq(0).text(t.$XML(n).find("PlayWndType").eq(0).text()), t.$XML(C).find("BuffNumberType").eq(0).text(t.$XML(n).find("BuffNumberType").eq(0).text()), t.$XML(C).find("RecordPath").eq(0).text(t.$XML(n).find("RecordPath").eq(0).text()), t.$XML(C).find("CapturePath").eq(0).text(t.$XML(n).find("CapturePath").eq(0).text()), t.$XML(C).find("PlaybackFilePath").eq(0).text(t.$XML(n).find("PlaybackFilePath").eq(0).text()), t.$XML(C).find("DeviceCapturePath").eq(0).text(t.$XML(n).find("DeviceCapturePath").eq(0).text()), t.$XML(C).find("PlaybackPicPath").eq(0).text(t.$XML(n).find("PlaybackPicPath").eq(0).text()), t.$XML(C).find("DownloadPath").eq(0).text(t.$XML(n).find("DownloadPath").eq(0).text()), t.$XML(C).find("IVSMode").eq(0).text(t.$XML(n).find("IVSMode").eq(0).text()), t.$XML(C).find("CaptureFileFormat").eq(0).text(t.$XML(n).find("CaptureFileFormat").eq(0).text()), r = f.HWP_SetLocalConfig(S.toXMLStr(C)), r ? 0 : -1
            };
            var ht = function (e, t, n, r, i, s, a) {
                var u = {protocol: t, success: null, error: null};
                S.extend(u, a), S.extend(u, {
                    success: function (u) {
                        var l = new o;
                        l.szIP = e, 2 == t ? (l.szHttpProtocol = "https://", l.iHttpsPort = n) : (l.szHttpProtocol = "http://", l.iHttpPort = n), l.iCGIPort = n, l.szDeviceIdentify = e + "_" + n, l.szAuth = r, l.iDeviceProtocol = i, l.oProtocolInc = s, O("使用%s协议登录成功", i), Q(l), tt(), et(l), m.push($.extend({}, l)), a.success && a.success(u)
                    }, error: function (e, t) {
                        a.error && a.error(e, t)
                    }
                }), s.login(e, n, r, u)
            };
            this.getAuthType = function (e, n, r, i, o, s) {
                e.oAuthType[n] = 1;
                var a = g, u = {
                    async: !0, success: function (r) {
                        e.oAuthType[n] = t.$XML(r).find("sessionIDVersion").eq(0).text(), s()
                    }, error: function (t) {
                        t > 500 && (e.oAuthType[n] = t), s(t)
                    }
                };
                a.getSessionCap(n, r, i, o, u)
            }, this.setDeviceInfo = function (e, t, n, r, i) {
                var t = new o;
                return t.szIP = n, 2 == r ? (t.szHttpProtocol = "https://", t.iHttpsPort = i) : (t.szHttpProtocol = "http://", t.iHttpPort = i), t.iCGIPort = i, t.szDeviceIdentify = n + "_" + i, t.iDeviceProtocol = b, t.oProtocolInc = e, t
            }, this.successV1cb = function (e, n, r, i, o, s, a, u) {
                n = this.setDeviceInfo(e, n, r, i, o), n.szAuth = t.$XML(s).find("sessionID").eq(0).text(), Q(n), O("使用%s协议登录成功", b), J(n.szDeviceIdentify, a, u), n.sessionFailed = 0, et(n);
                var l = $.extend({}, n);
                m.push(l), l.sesstionTimer = setInterval(function () {
                    e.sessionHeartbeat(l, function () {
                        l.sessionFailed = 0
                    }, function () {
                        l.sessionFailed++, l.sessionFailed >= 5 && (window.PluginEventHandler(null, F, l.szDeviceIdentify), clearInterval(l.sesstionTimer))
                    })
                }, 3e4)
            }, this.successV2cb = function (e, t, n, r, i, o, s, a) {
                t = this.setDeviceInfo(e, t, n, r, i), t.szAuth = Z(t, t.szDeviceIdentify), Q(t), O("使用%s协议登录成功", b), document.cookie = "domain='10.18.99.194';path=/;", et(t), J(t.szDeviceIdentify, s, a), t.sessionFailed = 0;
                var u = $.extend({}, t);
                m.push(u), u.sesstionTimer = setInterval(function () {
                    e.sessionHeartbeat(u, function () {
                        u.sessionFailed = 0
                    }, function () {
                        u.sessionFailed++, u.sessionFailed >= 5 && (window.PluginEventHandler(null, F, u.szDeviceIdentify), clearInterval(u.sesstionTimer))
                    })
                }, 3e4)
            }, this.I_LoginV1 = function (e, t, n, r, i, o, s, a) {
                var u = this, l = {success: null, error: null};
                return S.extend(l, {
                    success: function (l) {
                        var c = {success: null, error: null};
                        S.extend(c, {
                            success: function (l) {
                                a.success && (a.success(l), u.successV1cb(e, t, n, r, i, l, s, o))
                            }, error: function (e, t) {
                                a.error && a.error(e, t)
                            }
                        }), e.sessionLogin(n, r, i, o, s, l, c)
                    }, error: function (e, t) {
                        a.error && a.error(e, t)
                    }
                }), l
            }, this.I_LoginV2 = function (e, t, n, r, i, o, s, a, u) {
                var l = {success: null, error: null}, c = this;
                return S.extend(l, {
                    success: function (l) {
                        var d = {success: null, error: null};
                        S.extend(d, {
                            success: function (e) {
                                c.successV2cb(t, n, r, i, o, e, a, s), u.success && u.success(e)
                            }, error: function (e, t) {
                                u.error && u.error(e, t)
                            }
                        }), t.sessionV2Login(e, r, i, o, s, a, l, d)
                    }, error: function (e, t) {
                        u.error && u.error(e, t)
                    }
                }), l
            }, this.I_Login = function (e, t, n, r, i, s) {
                var a = e + "_" + n, u = this.findDeviceIndexByIP(a);
                if (-1 != u) return O("设备已经登录过"), -1;
                var l = g, c = b;
                if (j(s.cgi) || (b == s.cgi ? (l = g, c = b) : (l = P, c = x)), lt()) if (b == c) {
                    var d = new o;
                    this.getAuthType(d, e, t, n, r, function () {
                        var o = d.oAuthType[e];
                        if (o > 2) s.error && s.error(o); else if (2 > o) {
                            var a = this.I_LoginV1(l, d, e, t, n, r, i, s);
                            l.getSessionCap(e, t, n, r, a)
                        } else {
                            var u = MD5((new Date).getTime().toString()).substring(0, 8);
                            u = parseInt(u.replace("#", ""), 16).toString().substring(0, 8);
                            var a = this.I_LoginV2(u, l, d, e, t, n, r, i, s);
                            l.getSessionV2Cap(u, e, t, n, r, a)
                        }
                    })
                } else s.error && s.error(403, ft()); else {
                    var f = "";
                    if (b == c) {
                        f = S.Base64.encode(":" + r + ":" + i);
                        var h = {success: null, error: null};
                        S.extend(h, s), S.extend(h, {
                            error: function (o, a) {
                                f = S.Base64.encode(r + ":" + i), c = b, l = g;
                                var u = {success: null, error: null};
                                S.extend(u, s), S.extend(u, {
                                    error: function () {
                                        if (!j(s.cgi)) return s.error && s.error(o, a), void 0;
                                        f = S.Base64.encode(":" + r + ":" + i), c = x, l = P;
                                        var u = {success: null, error: null};
                                        S.extend(u, s), S.extend(u, {
                                            error: function (o, a) {
                                                f = S.Base64.encode(r + ":" + i), c = x, l = P;
                                                var u = {success: null, error: null};
                                                S.extend(u, s), S.extend(u, {
                                                    error: function () {
                                                        s.error && s.error(o, a)
                                                    }
                                                }), ht(e, t, n, f, c, l, u)
                                            }
                                        }), ht(e, t, n, f, c, l, u)
                                    }
                                }), ht(e, t, n, f, c, l, u)
                            }
                        }), ht(e, t, n, f, c, l, h)
                    } else {
                        f = S.Base64.encode(":" + r + ":" + i), c = x, l = P;
                        var h = {success: null, error: null};
                        S.extend(h, s), S.extend(h, {
                            error: function (o, a) {
                                f = S.Base64.encode(r + ":" + i), c = x, l = P;
                                var u = {success: null, error: null};
                                S.extend(u, s), S.extend(u, {
                                    error: function () {
                                        s.error && s.error(o, a)
                                    }
                                }), ht(e, t, n, f, c, l, u)
                            }
                        }), ht(e, t, n, f, c, l, h)
                    }
                }
            }, this.I_Logout = function (e) {
                var t = this.findDeviceIndexByIP(e);
                if (-1 != t) {
                    if (lt()) {
                        var n = m[t];
                        clearInterval(n.sesstionTimer), n.oProtocolInc.sessionLogout(n, {})
                    }
                    return m.splice(t, 1), 0
                }
                return -1
            }, this.I_GetAudioInfo = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {success: null, error: null};
                    S.extend(i, t), r.oProtocolInc.getAudioInfo(r, i)
                }
            }, this.I_GetDeviceInfo = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {success: null, error: null};
                    S.extend(i, t), r.oProtocolInc.getDeviceInfo(r, i)
                }
            }, this.I_GetAnalogChannelInfo = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {success: null, error: null};
                    S.extend(i, t), r.oProtocolInc.getAnalogChannelInfo(r, i)
                }
            }, this.I_GetSecurityVersion = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {success: null, error: null};
                    S.extend(i, t), r.oProtocolInc.getSecurityVersion(r, i)
                }
            }, this.I_GetDigitalChannelInfo = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {success: null, error: null};
                    S.extend(i, t), r.oProtocolInc.getDigitalChannelInfo(r, i)
                }
            }, this.I_GetZeroChannelInfo = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {success: null, error: null};
                    S.extend(i, t), r.oProtocolInc.getZeroChannelInfo(r, i)
                }
            }, this.getStream = function (e, t, n) {
                var r = $.Deferred();
                return lt() && f.JS_Play(e, t, n).then(function () {
                    addToWndSet(), r.resolve()
                }, function () {
                    r.reject()
                }), r
            }, this.I_StartRealPlay = function (e, n) {
                var r = this.findDeviceIndexByIP(e), i = "", o = "", s = -1, a = 0, u = 0, l = !1,
                    c = {iWndIndex: h, iStreamType: 1, iChannelID: 1, bZeroChannel: !1};
                if (S.extend(c, n), -1 != r) {
                    at(m[r]);
                    var d = m[r];
                    if (lt()) {
                        if (!d.bSupportWebsocket) return n.error && n.error(403, ft()), void 0;
                        i = d.oProtocolInc.CGI.startWsRealPlay;
                        var f = location.protocol;
                        o = /^(https)(.*)$/.test(f) ? "wss://" : "ws://", d.iWSPort = j(c.iWSPort) ? 7681 : c.iWSPort, s = d.iWSPort, u = c.iStreamType, a = c.iChannelID <= d.iAnalogChannelNum ? c.iChannelID : d.oStreamCapa.iIpChanBase + parseInt(c.iChannelID, 10) - d.iAnalogChannelNum - 1, l = !0
                    } else {
                        var p = parseInt(t.$XML(C).find("ProtocolType").eq(0).text(), 10);
                        p == B && d.oStreamCapa.bSupportShttpPlay ? (O("SHTTP RealPlay"), i = d.oProtocolInc.CGI.startShttpRealPlay, o = "http://", u = c.iStreamType - 1, a = c.iChannelID <= d.iAnalogChannelNum ? c.iChannelID : d.oStreamCapa.iIpChanBase + parseInt(c.iChannelID, 10) - d.iAnalogChannelNum - 1, l = !0, j(c.iPort) ? "https://" == d.szHttpProtocol ? (-1 == d.iHttpPort && (d.iHttpPort = nt(d).iHttpPort), s = d.iHttpPort) : s = d.iCGIPort : (d.iHttpPort = c.iPort, s = c.iPort)) : (O("RTSP RealPlay"), i = d.oProtocolInc.CGI.startRealPlay, o = "rtsp://", u = c.iStreamType, a = c.iChannelID, j(c.iRtspPort) || (d.iRtspPort = c.iRtspPort), -1 == d.iRtspPort && (d.iRtspPort = nt(d).iRtspPort), s = d.iRtspPort)
                    }
                    if (-1 == s) return O("获取端口号失败"), n.error && n.error(), void 0;
                    S.extend(c, {
                        urlProtocol: o,
                        cgi: i,
                        iPort: s,
                        iStreamType: u,
                        iChannelID: a
                    }), r = this.findWndIndexByIndex(c.iWndIndex);
                    var v = this;
                    -1 == r && d.oProtocolInc.startRealPlay(d, c).then(function () {
                        r = v.findWndIndexByIndex(c.iWndIndex);
                        var e = y[r];
                        e.bShttpIPChannel = l, n.success && n.success()
                    }, function () {
                        lt() || (d.iRtspPort = -1), n.error && n.error()
                    })
                } else n.error && n.error()
            }, this.I_CloseWin = function (e) {
                var t = this.findWndIndexByIndex(e);
                y.splice(t, 1)
            }, this.I_Stop = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n];
                    if (r.bRecord && (lt() || f.HWP_StopSave(r.iIndex)), r.bSound && (lt() || f.HWP_CloseSound()), r.bEZoom && (lt() || f.HWP_DisableZoom(r.iIndex)), lt()) f.JS_Stop(t.iWndIndex).then(function () {
                        y.splice(n, 1), t.success && t.success()
                    }, function () {
                        t.error && t.error()
                    }); else {
                        var i = f.HWP_Stop(t.iWndIndex);
                        0 == i ? (y.splice(n, 1), t.success && t.success()) : t.error && t.error()
                    }
                } else t.error && t.error()
            }, this.I_OpenSound = function (e) {
                e = j(e) ? h : e;
                var t = this.findWndIndexByIndex(e), n = -1;
                if (-1 != t) {
                    var r = y[t];
                    r.bSound || (n = lt() ? f.JS_OpenSound(e) : f.HWP_OpenSound(e), 0 == n && (r.bSound = !0))
                }
                return n
            }, this.I_CloseSound = function (e) {
                e = j(e) ? h : e;
                var t = this.findWndIndexByIndex(e), n = -1;
                if (-1 != t) {
                    var r = y[t];
                    r.bSound && (n = lt() ? f.JS_CloseSound() : f.HWP_CloseSound(), 0 == n && (r.bSound = !1))
                }
                return n
            }, this.I_SetVolume = function (e, t) {
                var n = -1;
                if (e = parseInt(e, 10), isNaN(e)) return n;
                if (0 > e || e > 100) return n;
                t = j(t) ? h : t;
                var r = this.findWndIndexByIndex(t);
                return -1 != r && (lt() ? (f.JS_SetVolume(t, e), n = 0) : n = f.HWP_SetVolume(t, e)), n
            }, this.I2_CapturePic = function (e, t) {
                var n = {iWndIndex: h, bDateDir: !0};
                V(t) ? S.extend(n, t) : j(t) || (n.iWndIndex = t);
                var r = this.findWndIndexByIndex(n.iWndIndex), i = -1, o = $.Deferred();
                if (-1 != r) if (lt()) {
                    var s = "JPEG";
                    ".jpg" === e.slice(-4).toLowerCase() ? e = e.slice(0, -4) : ".jpeg" === e.slice(-5).toLowerCase() ? e = e.slice(0, -5) : ".bmp" === e.slice(-4).toLowerCase() && (e = e.slice(0, -4), s = "BMP"), f.JS_CapturePicture(n.iWndIndex, e, s).then(function () {
                        o.resolve()
                    }, function () {
                        o.reject()
                    })
                } else ".jpg" === e.slice(-4).toLowerCase() ? e = e.slice(0, -4) : ".jpeg" === e.slice(-5).toLowerCase() && (e = e.slice(0, -5)), i = f.HWP_CapturePicture(n.iWndIndex, e, n.bDateDir), 0 === i ? o.resolve() : o.reject(); else o.reject();
                return o
            }, this.I2_CapturePicData = function (e) {
                var t = {iWndIndex: h, bDateDir: !0};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = $.Deferred();
                    lt() ? f.JS_CapturePictureData(t.iWndIndex).then(function (e) {
                        r.resolve(e)
                    }, function (e) {
                        O(e), r.reject()
                    }) : r.reject()
                } else r.reject();
                return r
            }, this.I_StartRecord = function (e, t) {
                var n = {iWndIndex: h, bDateDir: !0};
                V(t) ? S.extend(n, t) : j(t) || (n.iWndIndex = t);
                var r = this.findWndIndexByIndex(n.iWndIndex);
                if (-1 != r) {
                    var i = y[r];
                    if (i.bRecord) n.error && n.error(); else if (lt()) {
                        var o = S.browser();
                        o.chrome ? f.JS_StartSave(n.iWndIndex, e).then(function () {
                            i.bRecord = !0, n.success && n.success()
                        }, function () {
                            n.error && n.error()
                        }) : n.error && n.error()
                    } else {
                        var s = f.HWP_StartSave(n.iWndIndex, e, n.bDateDir);
                        0 == s ? (i.bRecord = !0, n.success && n.success()) : n.error && n.error()
                    }
                } else n.error && n.error()
            }, this.I_StopRecord = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n];
                    if (r.bRecord) if (lt()) {
                        var i = S.browser();
                        i.chrome ? f.JS_StopSave(t.iWndIndex).then(function () {
                            r.bRecord = !1, t.success && t.success()
                        }, function () {
                            t.error && t.error()
                        }) : t.error && t.error()
                    } else {
                        var o = f.HWP_StopSave(t.iWndIndex);
                        0 == o ? (r.bRecord = !1, t.success && t.success()) : t.error && t.error()
                    } else t.error && t.error()
                } else t.error && t.error()
            }, this.I_StartVoiceTalk = function (e, t) {
                if (isNaN(parseInt(t, 10))) return -1;
                var n = this.findDeviceIndexByIP(e), r = -1;
                if (-1 != n) {
                    var i = m[n];
                    i.bVoiceTalk || (r = lt() ? -1 : i.oProtocolInc.startVoiceTalk(i, t), 0 == r && (m[n].bVoiceTalk = !0))
                }
                return r
            }, this.I_StopVoiceTalk = function () {
                var e = -1;
                if (!lt()) {
                    e = f.HWP_StopVoiceTalk();
                    for (var t = 0, n = m.length; n > t; t++) if (m[t].bVoiceTalk) {
                        m[t].bVoiceTalk = !1;
                        break
                    }
                }
                return e
            }, this.I_PTZControl = function (e, t, n) {
                var r = {iWndIndex: h, iPTZIndex: e, iPTZSpeed: 4};
                S.extend(r, n), S.extend(r, {async: !1});
                var i = this.findWndIndexByIndex(r.iWndIndex);
                if (-1 != i) {
                    var o = y[i];
                    if (i = this.findDeviceIndexByIP(o.szIP), -1 != i) {
                        var s = m[i];
                        9 == e ? s.oProtocolInc.ptzAutoControl(s, t, o, r) : s.oProtocolInc.ptzControl(s, t, o, r)
                    }
                }
            }, this.I_EnableEZoom = function (e) {
                e = j(e) ? h : e;
                var t = this.findWndIndexByIndex(e), n = -1;
                if (-1 != t) {
                    var r = y[t];
                    r.bEZoom || (n = lt() ? f.JS_EnableZoom(e) : f.HWP_EnableZoom(e, 0), 0 == n && (r.bEZoom = !0))
                }
                return n
            }, this.I_DisableEZoom = function (e) {
                e = j(e) ? h : e;
                var t = this.findWndIndexByIndex(e), n = -1;
                if (-1 != t) {
                    var r = y[t];
                    r.bEZoom && (lt() ? n = f.JS_DisableZoom(e) : (f.HWP_DisableZoom(e), n = 0), 0 == n && (r.bEZoom = !1))
                }
                return n
            }, this.I_Enable3DZoom = function (e) {
                e = j(e) ? h : e;
                var t = this.findWndIndexByIndex(e), n = -1;
                if (-1 != t) {
                    var r = y[t];
                    r.b3DZoom || (lt() ? (n = 0, f.JS_Enable3DZoom(e, function (e) {
                        window.ZoomInfoCallback(e)
                    })) : n = f.HWP_EnableZoom(e, 1), 0 == n && (r.b3DZoom = !0))
                }
                return n
            }, this.I_Disable3DZoom = function (e) {
                e = j(e) ? h : e;
                var t = this.findWndIndexByIndex(e), n = -1;
                if (-1 != t) {
                    var r = y[t];
                    r.b3DZoom && (lt() ? (f.JS_Disable3DZoom(e), n = 0) : (f.HWP_DisableZoom(e), n = 0), 0 == n && (r.b3DZoom = !1))
                }
                return n
            }, this.I_FullScreen = function (e) {
                lt() ? f.JS_FullScreenSingle(h) : f.HWP_FullScreenDisplay(e)
            }, this.I_SetPreset = function (e, t) {
                var n = {iWndIndex: h, iPresetID: e};
                S.extend(n, t);
                var r = this.findWndIndexByIndex(n.iWndIndex);
                if (-1 != r) {
                    var i = y[r];
                    if (r = this.findDeviceIndexByIP(i.szIP), -1 != r) {
                        var o = m[r];
                        o.oProtocolInc.setPreset(o, i, n)
                    }
                }
            }, this.I_GoPreset = function (e, t) {
                var n = {iWndIndex: h, iPresetID: e};
                S.extend(n, t);
                var r = this.findWndIndexByIndex(n.iWndIndex);
                if (-1 != r) {
                    var i = y[r];
                    if (r = this.findDeviceIndexByIP(i.szIP), -1 != r) {
                        var o = m[r];
                        o.oProtocolInc.goPreset(o, i, n)
                    }
                }
            }, this.I_RecordSearch = function (e, t, n, r, i) {
                var o = this.findDeviceIndexByIP(e);
                if (-1 != o) {
                    var s = m[o], a = {
                        iChannelID: t,
                        szStartTime: n,
                        szEndTime: r,
                        iSearchPos: 0,
                        iStreamType: 1,
                        success: null,
                        error: null
                    };
                    S.extend(a, i), s.oProtocolInc.recordSearch(s, a)
                }
            }, this.I_StartPlayback = function (e, n) {
                var r = this.findDeviceIndexByIP(e), i = "", o = "", s = -1, a = 1, u = 0,
                    l = S.dateFormat(new Date, "yyyy-MM-dd"), c = {
                        iWndIndex: h,
                        iStreamType: 1,
                        iChannelID: 1,
                        szStartTime: l + " 00:00:00",
                        szEndTime: l + " 23:59:59"
                    };
                if (S.extend(c, n), -1 != r) {
                    at(m[r]);
                    var d = m[r];
                    if (lt()) {
                        if (!d.bSupportWebsocket) return n.error && n.error(403, ft()), void 0;
                        if (!j(c.oTransCodeParam)) return n.error && n.error(), void 0;
                        i = d.oProtocolInc.CGI.startWsPlayback;
                        var f = location.protocol;
                        o = /^(https)(.*)$/.test(f) ? "wss://" : "ws://", d.iWSPort = j(c.iWSPort) ? 7681 : c.iWSPort, s = d.iWSPort, u = c.iStreamType, a = c.iChannelID <= d.iAnalogChannelNum ? c.iChannelID : d.oStreamCapa.iIpChanBase + parseInt(c.iChannelID, 10) - d.iAnalogChannelNum - 1, a = 100 * a + u
                    } else {
                        var p = parseInt(t.$XML(C).find("ProtocolType").eq(0).text(), 10);
                        p == B && d.oStreamCapa.bSupportShttpPlay ? (i = j(c.oTransCodeParam) ? d.oProtocolInc.CGI.startShttpPlayback : d.oProtocolInc.CGI.startTransCodePlayback, o = "http://", u = c.iStreamType - 1, a = c.iChannelID <= d.iAnalogChannelNum ? c.iChannelID : d.oStreamCapa.iIpChanBase + parseInt(c.iChannelID, 10) - d.iAnalogChannelNum - 1, d.bSupportSubStreamPlayback && (a = 100 * a + u), j(c.iPort) ? "https://" == d.szHttpProtocol ? (-1 == d.iHttpPort && (d.iHttpPort = nt(d).iHttpPort), s = d.iHttpPort) : s = d.iCGIPort : (d.iHttpPort = c.iPort, s = c.iPort)) : (i = d.oProtocolInc.CGI.startPlayback, o = "rtsp://", u = c.iStreamType, a = 100 * c.iChannelID + u, j(c.iRtspPort) || (d.iRtspPort = c.iRtspPort), -1 == d.iRtspPort && (d.iRtspPort = nt(d).iRtspPort), s = d.iRtspPort)
                    }
                    if (-1 == s) return O("获取端口号失败"), n.error && n.error(), void 0;
                    S.extend(c, {
                        urlProtocol: o,
                        cgi: i,
                        iPort: s,
                        iChannelID: a
                    }), r = this.findWndIndexByIndex(c.iWndIndex), -1 == r && (lt() ? (c.szStartTime = c.szStartTime.replace(" ", "T") + "Z", c.szEndTime = c.szEndTime.replace(" ", "T") + "Z", d.oProtocolInc.startPlayback(d, c).then(function () {
                        n.success && n.success()
                    }, function () {
                        n.error && n.error()
                    })) : (c.szStartTime = c.szStartTime.replace(/[-:]/g, "").replace(" ", "T") + "Z", c.szEndTime = c.szEndTime.replace(/[-:]/g, "").replace(" ", "T") + "Z", d.oProtocolInc.startPlayback(d, c).then(function () {
                        n.success && n.success()
                    }, function () {
                        d.iRtspPort = -1, n.error && n.error()
                    })))
                } else n.error && n.error()
            }, this.I_ReversePlayback = function (e, n) {
                var r = this.findDeviceIndexByIP(e), i = -1, o = "", s = "", a = -1, u = -1, l = 0,
                    c = S.dateFormat(new Date, "yyyy-MM-dd"), d = {
                        iWndIndex: h,
                        iStreamType: 1,
                        iChannelID: 1,
                        szStartTime: c + " 00:00:00",
                        szEndTime: c + " 23:59:59"
                    };
                if (S.extend(d, n), -1 != r) {
                    at(m[r]);
                    var f = m[r];
                    if (lt()) return i;
                    var p = parseInt(t.$XML(C).find("ProtocolType").eq(0).text(), 10);
                    if (p == B && f.oStreamCapa.bSupportShttpPlay ? (o = f.oProtocolInc.CGI.startShttpReversePlayback, s = "http://", l = d.iStreamType - 1, u = d.iChannelID <= f.iAnalogChannelNum ? d.iChannelID : f.oStreamCapa.iIpChanBase + parseInt(d.iChannelID, 10) - f.iAnalogChannelNum - 1, u = 100 * u + l, j(d.iPort) ? "https://" == f.szHttpProtocol ? (-1 == f.iHttpPort && (f.iHttpPort = nt(f).iHttpPort), a = f.iHttpPort) : a = f.iCGIPort : (f.iHttpPort = d.iPort, a = d.iPort)) : (o = f.oProtocolInc.CGI.startPlayback, s = "rtsp://", l = d.iStreamType, u = 100 * d.iChannelID + l, j(d.iRtspPort) || (f.iRtspPort = d.iRtspPort), -1 == f.iRtspPort && (f.iRtspPort = nt(f).iRtspPort), a = f.iRtspPort), -1 == a) return O("获取端口号失败"), i;
                    S.extend(d, {
                        urlProtocol: s,
                        cgi: o,
                        iPort: a,
                        iChannelID: u
                    }), r = this.findWndIndexByIndex(d.iWndIndex), -1 == r && (d.szStartTime = d.szStartTime.replace(/[-:]/g, "").replace(" ", "T") + "Z", d.szEndTime = d.szEndTime.replace(/[-:]/g, "").replace(" ", "T") + "Z", i = f.oProtocolInc.reversePlayback(f, d))
                }
                return -1 == i && (f.iRtspPort = -1), i
            }, this.I_Frame = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n], i = r.iPlayStatus;
                    if (i == D || i == M) if (lt()) f.JS_FrameForward(t.iWndIndex).then(function () {
                        r.iPlayStatus = M, t.success && t.success()
                    }, function () {
                        t.error && t.error()
                    }); else {
                        var o = f.HWP_FrameForward(t.iWndIndex);
                        0 == o ? (r.iPlayStatus = M, t.success && t.success()) : t.error && t.error()
                    } else t.error && t.error()
                } else t.error && t.error()
            }, this.I_Pause = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n], i = r.iPlayStatus, o = -1;
                    if (i == D) o = k; else {
                        if (i != z) return t.error && t.error(), void 0;
                        o = R
                    }
                    if (lt()) f.JS_Pause(t.iWndIndex).then(function () {
                        r.iPlayStatus = o, t.success && t.success()
                    }, function () {
                        t.error && t.error()
                    }); else {
                        var s = f.HWP_Pause(t.iWndIndex);
                        0 == s ? (r.iPlayStatus = o, t.success && t.success()) : t.error && t.error()
                    }
                } else t.error && t.error()
            }, this.I_Resume = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n], i = r.iPlayStatus, o = -1;
                    if (i == k || i == M) o = D; else {
                        if (i != R) return t.error && t.error(), void 0;
                        o = z
                    }
                    if (lt()) f.JS_Resume(t.iWndIndex).then(function () {
                        r.iPlayStatus = o, t.success && t.success()
                    }, function () {
                        t.error && t.error()
                    }); else {
                        var s = f.HWP_Resume(t.iWndIndex);
                        0 == s ? (r.iPlayStatus = o, t.success && t.success()) : t.error && t.error()
                    }
                } else t.error && t.error()
            }, this.I_PlaySlow = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n];
                    if (r.iPlayStatus == D) if (lt()) {
                        var i = f.JS_GetWndStatus(t.iWndIndex);
                        i.iRate <= -4 ? t.error && t.error() : f.JS_Slow(t.iWndIndex).then(function () {
                            t.success && t.success()
                        }, function () {
                            t.error && t.error()
                        })
                    } else {
                        var o = f.HWP_Slow(t.iWndIndex);
                        0 == o ? t.success && t.success() : t.error && t.error()
                    } else t.error && t.error()
                } else t.error && t.error()
            }, this.I_PlayFast = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    var r = y[n];
                    if (r.iPlayStatus == D) if (lt()) {
                        var i = f.JS_GetWndStatus(t.iWndIndex);
                        i.iRate >= 8 ? t.error && t.error() : f.JS_Fast(t.iWndIndex).then(function () {
                            t.success && t.success()
                        }, function () {
                            t.error && t.error()
                        })
                    } else {
                        var o = f.HWP_Fast(t.iWndIndex);
                        0 == o ? t.success && t.success() : t.error && t.error()
                    } else t.error && t.error()
                } else t.error && t.error()
            }, this.I_GetOSDTime = function (e) {
                var t = {iWndIndex: h};
                V(e) ? S.extend(t, e) : j(e) || (t.iWndIndex = e);
                var n = this.findWndIndexByIndex(t.iWndIndex);
                if (-1 != n) {
                    if (lt()) f.JS_GetOSDTime(t.iWndIndex).then(function (e) {
                        if (t.success) {
                            var n = S.dateFormat(new Date(1e3 * e), "yyyy-MM-dd hh:mm:ss");
                            t.success(n)
                        }
                    }, function () {
                        t.error && t.error()
                    }); else if (t.success) {
                        var r = f.HWP_GetOSDTime(t.iWndIndex),
                            i = S.dateFormat(new Date(1e3 * r), "yyyy-MM-dd hh:mm:ss");
                        t.success(i)
                    }
                } else t.error && t.error()
            }, this.I_StartDownloadRecord = function (e, t, n, r) {
                var i = this.findDeviceIndexByIP(e), o = -1;
                if (-1 != i) {
                    var s = m[i], a = {szPlaybackURI: t, szFileName: n, bDateDir: !0};
                    j(r) || S.extend(a, r), o = s.oProtocolInc.startDownloadRecord(s, a)
                }
                return o
            }, this.I_StartDownloadRecordByTime = function (e, t, n, r, i, o) {
                var s = this.findDeviceIndexByIP(e), a = -1;
                if (-1 != s) {
                    var u = m[s];
                    t = t.split("?")[0] + "?starttime=" + r.replace(" ", "T") + "Z&endtime=" + i.replace(" ", "T") + "Z";
                    var l = {szPlaybackURI: t, szFileName: n, bDateDir: !0};
                    j(o) || S.extend(l, o), a = u.oProtocolInc.startDownloadRecord(u, l)
                }
                return a
            }, this.I_GetDownloadStatus = function (e) {
                var t = -1;
                return lt() ? t : (t = f.HWP_GetDownloadStatus(e), 1 == t && (t = -1), t)
            }, this.I_GetDownloadProgress = function (e) {
                return lt() ? -1 : f.HWP_GetDownloadProgress(e)
            }, this.I_StopDownloadRecord = function (e) {
                return lt() ? -1 : f.HWP_StopDownload(e)
            }, this.I_ExportDeviceConfig = function (e, t) {
                var n = this.findDeviceIndexByIP(e), r = -1;
                if (-1 != n) {
                    var i = m[n];
                    r = i.oProtocolInc.exportDeviceConfig(i, t)
                }
                return r
            }, this.I_ImportDeviceConfig = function (e, t, n) {
                var r = this.findDeviceIndexByIP(e), i = -1;
                if (-1 != r) {
                    var o = m[r], s = {szFileName: t};
                    i = o.oProtocolInc.importDeviceConfig(o, s, n)
                }
                return i
            }, this.I_RestoreDefault = function (e, t, n) {
                var r = {success: null, error: null};
                S.extend(r, n);
                var i = this.findDeviceIndexByIP(e);
                if (-1 != i) {
                    var o = m[i];
                    o.oProtocolInc.restore(o, t, r)
                }
            }, this.I_Restart = function (e, t) {
                var n = this.findDeviceIndexByIP(e), r = {success: null, error: null};
                if (S.extend(r, t), -1 != n) {
                    var i = m[n];
                    i.oProtocolInc.restart(i, r)
                }
            }, this.I_Reconnect = function (e, t) {
                var n = this.findDeviceIndexByIP(e), r = {success: null, error: null};
                if (S.extend(r, t), -1 != n) {
                    var i = m[n];
                    i.oProtocolInc.login(i.szIP, i.iCGIPort, i.szAuth, r)
                }
            }, this.I_StartUpgrade = function (e, t) {
                var n = this.findDeviceIndexByIP(e), r = -1;
                if (-1 != n) {
                    var i = m[n], o = {szFileName: t};
                    return lt() ? r : i.oProtocolInc.startUpgrade(i, o)
                }
                return r
            }, this.I2_StartUpgrade = function (e, t) {
                var n = this.findDeviceIndexByIP(e);
                if (-1 != n) {
                    var r = m[n], i = {szFileName: t};
                    return r.oProtocolInc.asyncstartUpgrade(r, i)
                }
            }, this.I_UpgradeStatus = function () {
                return lt() ? f.JS_UpgradeStatus() : f.HWP_UpgradeStatus()
            }, this.I_UpgradeProgress = function () {
                return lt() ? f.JS_UpgradeProgress() : f.HWP_UpgradeProgress()
            }, this.I_StopUpgrade = function () {
                return lt() ? -1 : f.HWP_StopUpgrade()
            }, this.I_CheckPluginInstall = function () {
                var e = -1, t = S.browser();
                if (lt()) e = 0; else if (t.msie) try {
                    new ActiveXObject("WebVideoKitActiveX.WebVideoKitActiveXCtrl.1"), e = 0
                } catch (n) {
                } else for (var r = 0, i = navigator.mimeTypes.length; i > r; r++) if ("application/webvideo-plugin-kit" == navigator.mimeTypes[r].type.toLowerCase()) {
                    e = 0;
                    break
                }
                return e
            }, this.I_CheckPluginVersion = function () {
                return lt() ? 0 : f.HWP_CheckPluginUpdate(G) ? -1 : 0
            }, this.I_SendHTTPRequest = function (e, t, n) {
                var r = this.findDeviceIndexByIP(e);
                if (!(0 > r)) {
                    var i = m[r], o = new mt, s = i.szHttpProtocol + i.szIP + ":" + i.iCGIPort + "/" + t,
                        a = {type: "GET", url: s, auth: i.szAuth, success: null, error: null};
                    S.extend(a, n), S.extend(a, {
                        success: function (e) {
                            n.success && n.success(e)
                        }, error: function (e, t) {
                            n.error && n.error(e, t)
                        }
                    }), o.setRequestParam(a), o.submitRequest()
                }
            }, this.I_RemoteConfig = function (e, t) {
                var n = this.findDeviceIndexByIP(e), r = -1, i = -1;
                if (lt()) return r;
                var o = {iLan: 0, iDevicePort: -1, iType: 0};
                if (S.extend(o, t), -1 != n) {
                    var s = m[n];
                    if (-1 == o.iDevicePort) if (-1 == s.iDevicePort) {
                        if (s.iDevicePort = nt(s).iDevicePort, i = s.iDevicePort, -1 == i) return r
                    } else i = s.iDevicePort; else i = o.iDevicePort;
                    if (":" == S.Base64.decode(s.szAuth)[0]) var a = S.Base64.decode(s.szAuth).split(":")[1],
                        u = S.Base64.decode(s.szAuth).split(":")[2]; else var a = S.Base64.decode(s.szAuth).split(":")[0],
                        u = S.Base64.decode(s.szAuth).split(":")[1];
                    var l = "<RemoteInfo><DeviceInfo><DeviceType>" + o.iType + "</DeviceType>" + "<LanType>" + o.iLan + "</LanType>" + "<IP>" + s.szIP + "</IP>" + "<Port>" + i + "</Port>" + "<UName>" + a + "</UName>" + "<PWD>" + S.Base64.encode(u) + "</PWD></DeviceInfo></RemoteInfo>";
                    return f.HWP_ShowRemConfig(l)
                }
                return r
            }, this.I_ChangeWndNum = function (e) {
                return isNaN(parseInt(e, 10)) ? -1 : (lt() ? f.JS_ArrangeWindow(e) : f.HWP_ArrangeWindow(e), 0)
            }, this.I_GetLastError = function () {
                return lt() ? -1 : f.HWP_GetLastError()
            }, this.I_GetWindowStatus = function (e) {
                if (j(e)) {
                    var t = [];
                    return S.extend(t, y), t
                }
                var n = this.findWndIndexByIndex(e);
                if (-1 != n) {
                    var t = {};
                    return S.extend(t, y[n]), t
                }
                return null
            }, this.I_GetIPInfoByMode = function (e, t, n, r) {
                return lt() ? "" : f.HWP_GetIpInfoByMode(e, t, n, r)
            }, this.I_SetPlayModeType = function (e) {
                return lt() ? 0 : f.HWP_SetPlayModeType(e)
            }, this.I_SetSnapDrawMode = function (e, t) {
                return lt() ? t > -1 ? f.JS_SetDrawStatus(!0) : f.JS_SetDrawStatus(!1) : f.HWP_SetSnapDrawMode(e, t)
            }, this.I2_SetSnapPolygonInfo = function (r, o) {
                if (lt()) {
                    f.JS_GetPolygonInfo();
                    var s = S.loadXML(o), a = [];
                    for (nodeList = t.$XML(s).find("SnapPolygon", !0), i = 0, iLen = nodeList.length; iLen > i; i++) {
                        node = nodeList[i];
                        var u = e(t.$XML(node).find("r").eq(0).text(), t.$XML(node).find("g").eq(0).text(), t.$XML(node).find("b").eq(0).text());
                        u = n(u, 6), u = "#" + u;
                        for (var l = [], c = t.$XML(node).find("point", !0), d = 0, h = c.length; h > d; d++) oNodePoint = c[d], l.push([500 * t.$XML(oNodePoint).find("x").eq(0).text(), 300 * t.$XML(oNodePoint).find("y").eq(0).text()]);
                        a.push({
                            iPolygonType: 1,
                            id: t.$XML(node).find("id").eq(0).text(),
                            iEditType: 0,
                            aPoint: l,
                            bClosed: !0,
                            szTips: t.$XML(node).find("tips").eq(0).text(),
                            szDrawColor: u,
                            iTranslucent: .1
                        })
                    }
                    return f.JS_SetPolygonInfo(a)
                }
                return f.HWP_SetSnapPolygonInfo(r, o)
            }, this.I_SetSnapPolygonInfo = function (r, i) {
                if (lt()) {
                    var o = f.JS_GetPolygonInfo(), s = S.loadXML(i), a = [];
                    for (x = t.$XML(s).find("SnapPolygon", !0), d = 0, h = x.length; h > d; d++) {
                        p = x[d];
                        var u = e(t.$XML(p).find("r").eq(0).text(), t.$XML(p).find("g").eq(0).text(), t.$XML(p).find("b").eq(0).text());
                        u = n(u, 6), u = "#" + u, a.push({
                            id: t.$XML(p).find("id").eq(0).text(),
                            tips: t.$XML(p).find("tips").eq(0).text(),
                            iMaxShapeSupport: o.length + 1,
                            iMaxPointSupport: t.$XML(p).find("PointNumMax").eq(0).text(),
                            iMinPointSupport: t.$XML(p).find("MinClosed").eq(0).text(),
                            style: {szDrawColor: u, iTranslucent: .1}
                        })
                    }
                    0 === x.length && a.push({
                        iMaxShapeSupport: 1,
                        iMaxPointSupport: 17,
                        iMinPointSupport: 1,
                        style: {szDrawColor: "#FFFF00", iTranslucent: .1}
                    });
                    var l = [];
                    $.each(o, function (e, t) {
                        l.push(t.id)
                    });
                    var c = l.join(",");
                    if (c = "," + c + ",", o.length + a.length > 32) return -3;
                    for (var d = 0, h = a.length; h > d; d++) {
                        var p = a[d], m = p.id;
                        if (!S.isInt(m)) return -2;
                        var y = parseInt(m, 10);
                        if (1 > y || y > 32) return -2;
                        if (c.indexOf("," + m + ",") > -1) return -4;
                        var v = p.tips;
                        if (v.length > 32) return -2;
                        var g = p.flag;
                        if (!g) {
                            var P = p.iMinPointSupport;
                            if (!S.isInt(P)) return -2;
                            var I = parseInt(P, 10);
                            if (4 > I || I > 17) return -2;
                            var C = p.iMaxPointSupport;
                            if (!S.isInt(C)) return -2;
                            var b = parseInt(C, 10);
                            if (I > b || b > 17) return -2
                        }
                    }
                    return f.JS_SetDrawShapeInfo("Polygon", a[0])
                }
                var s, x, d, h, p, m, y, v, P, I, g, C, b, w = f.HWP_GetSnapPolygonInfo(r);
                s = S.loadXML(w), x = t.$XML(s).find("SnapPolygon", !0);
                var T = x.length, l = [];
                for (d = 0, h = x.length; h > d; d++) p = x[d], l.push(t.$XML(p).find("id").eq(0).text());
                var c = l.join(",");
                if (c = "," + c + ",", s = S.loadXML(i), x = t.$XML(s).find("SnapPolygon", !0), T + x.length > 32) return -3;
                for (d = 0, h = x.length; h > d; d++) {
                    if (p = x[d], m = t.$XML(p).find("id").eq(0).text(), !S.isInt(m)) return -2;
                    if (y = parseInt(m, 10), 1 > y || y > 32) return -2;
                    if (c.indexOf("," + m + ",") > -1) return -4;
                    if (v = t.$XML(p).find("tips").eq(0).text(), v.length > 32) return -2;
                    if (g = "true" === t.$XML(p).find("isClosed").eq(0).text(), !g) {
                        if (P = t.$XML(p).find("MinClosed").eq(0).text(), !S.isInt(P)) return -2;
                        if (I = parseInt(P, 10), 4 > I || I > 17) return -2;
                        if (C = t.$XML(p).find("PointNumMax").eq(0).text(), !S.isInt(C)) return -2;
                        if (b = parseInt(C, 10), I > b || b > 17) return -2
                    }
                }
                return f.HWP_SetSnapPolygonInfo(r, i)
            }, this.I_GetSnapPolygonInfo = function (e) {
                if (lt()) {
                    for (var t = f.JS_GetPolygonInfo(e), n = '<?xml version="1.0" encoding="utf-8"?><SnapPolygonList>', i = 0; i < t.length; i++) {
                        n += "<SnapPolygon>";
                        var o = r(t[i].szDrawColor);
                        n += "<id>" + t[i].szTips.split("#")[1] + "</id><polygonType>1</polygonType><color><r>" + o[0] + "</r><g>" + o[1] + "</g><b>" + o[2] + "</b></color><tips>" + t[i].szTips + "</tips><isClosed>" + t[i].bClosed + "</isClosed><pointList>";
                        for (var s = 0; s < t[i].aPoint.length; s++) n += "<point><x>" + t[i].aPoint[s][0] / a + "</x><y>" + t[i].aPoint[s][1] / u + "</y></point>";
                        n += "</pointList></SnapPolygon>"
                    }
                    return n += "</SnapPolygonList>"
                }
                return f.HWP_GetSnapPolygonInfo(e)
            }, this.I_ClearSnapInfo = function (e) {
                return lt() ? f.JS_ClearSnapInfo() : f.HWP_ClearSnapInfo(e, 1)
            }, this.I_DeviceCapturePic = function (e, t, n, r) {
                var i = this.findDeviceIndexByIP(e), o = -1;
                if (-1 != i) {
                    var s = m[i], a = {bDateDir: !0};
                    if (S.extend(a, r), !j(a.iResolutionWidth) && !S.isInt(a.iResolutionWidth)) return o;
                    if (!j(a.iResolutionHeight) && !S.isInt(a.iResolutionHeight)) return o;
                    o = s.oProtocolInc.deviceCapturePic(s, t, n, a)
                }
                return o
            }, this.I_SetPackageType = function (e) {
                return lt() ? -1 : f.HWP_SetPackageType(e)
            }, this.I_GetDevicePort = function (e) {
                var t = this.findDeviceIndexByIP(e), n = null;
                if (-1 != t) {
                    var r = m[t];
                    n = nt(r)
                }
                return n
            }, this.I_GetTextOverlay = function (e, t, n) {
                var r = this.findDeviceIndexByIP(t), i = -1;
                if (-1 != r) {
                    var o = m[r];
                    y[r];
                    var s = {async: !1, type: "GET", success: n.success, error: n.error};
                    this.I_SendHTTPRequest(o.szIP + "_" + o.iCGIPort, e, s)
                }
                return i
            }, this.findDeviceIndexByIP = function (e) {
                if (e.indexOf("_") > -1) {
                    for (var t = 0, n = m.length; n > t; t++) if (m[t].szDeviceIdentify == e) return t
                } else for (var t = 0, n = m.length; n > t; t++) if (m[t].szIP == e) return t;
                return -1
            }, this.findWndIndexByIndex = function (e) {
                for (var t = 0, n = y.length; n > t; t++) if (y[t].iIndex == e) return t;
                return -1
            };
            var pt = function () {
                this.iIndex = 0, this.szIP = "", this.iCGIPort = 80, this.szDeviceIdentify = "", this.iChannelID = "", this.iPlayStatus = T, this.bSound = !1, this.bRecord = !1, this.bPTZAuto = !1, this.bEZoom = !1, this.b3DZoom = !1
            }, mt = function () {
                this.options = {
                    type: "GET",
                    url: "",
                    auth: "",
                    timeout: 1e4,
                    data: "",
                    async: !0,
                    success: null,
                    error: null
                }, this.m_szHttpHead = "", this.m_szHttpContent = "", this.m_szHttpData = ""
            };
            mt.prototype.m_httpRequestSet = [], mt.prototype.setRequestParam = function (e) {
                S.extend(this.options, e)
            }, mt.prototype.submitRequest = function () {
                var e = null, t = this;
                if (lt()) {
                    this.options.auth ? S.cookie("WebSession", this.options.auth) : S.cookie("WebSession", null);
                    var n = ct(this.options.url), r = new window.XMLHttpRequest;
                    r.open(this.options.type, n, this.options.async);
                    var i = function (e) {
                        return /^(http|https):\/\/([^\/]+)(.+)$/.test(e) ? RegExp.$2 : ""
                    };
                    if (d.proxyAddress) {
                        r.withCredentials = !0;
                        var o = i(this.options.url);
                        r.setRequestHeader("deviceIdentify", o)
                    }
                    r.setRequestHeader("X-Requested-With", "XMLHttpRequest"), r.setRequestHeader("If-Modified-Since", "0"), r.send(this.options.data || null);
                    var s = function () {
                        if (4 === r.readyState) {
                            e = {funSuccessCallback: t.options.success, funErrorCallback: t.options.error};
                            var n = r.status + r.responseText;
                            0 === r.status && (n = ""), t.httpDataAnalyse(e, n)
                        }
                    };
                    this.options.async ? (r.timeout = this.options.timeout, r.onreadystatechange = function () {
                        s()
                    }) : s()
                } else {
                    var a = this.getHttpMethod(this.options.type);
                    if (this.options.async) {
                        var u = f.HWP_SubmitHttpRequest(a, this.options.url, this.options.auth, this.options.data, this.options.timeout);
                        -1 != u && (e = {
                            iRequestID: u,
                            funSuccessCallback: this.options.success,
                            funErrorCallback: this.options.error
                        }, this.m_httpRequestSet.push(e))
                    } else {
                        var l = f.HWP_SendHttpSynRequest(a, this.options.url, this.options.auth, this.options.data, this.options.timeout);
                        e = {
                            funSuccessCallback: this.options.success,
                            funErrorCallback: this.options.error
                        }, this.httpDataAnalyse(e, l)
                    }
                }
            }, mt.prototype.getHttpMethod = function (e) {
                var t = {GET: 1, POST: 2, PUT: 5, DELETE: 6}, n = t[e];
                return n ? n : -1
            }, mt.prototype.processCallback = function (e, t) {
                for (var n = null, r = 0; r < this.m_httpRequestSet.length; r++) if (e == this.m_httpRequestSet[r].iRequestID) {
                    n = this.m_httpRequestSet[r], this.m_httpRequestSet.splice(r, 1);
                    break
                }
                null != n && (this.httpDataAnalyse(n, t), delete n)
            }, mt.prototype.httpDataAnalyse = function (e, t) {
                var n = "", r = 0;
                "" == t || j(t) ? e.funErrorCallback() : (r = parseInt(t.substring(0, 3)), n = t.substring(3, t.length), isNaN(r) ? e.funErrorCallback() : w == r ? this.options && this.options.url.indexOf("?format=json") > -1 ? e.funSuccessCallback(JSON.parse(n)) : e.funSuccessCallback(S.loadXML(n)) : e.funErrorCallback && e.funErrorCallback(r, S.loadXML(n)))
            };
            var yt = function () {
            };
            yt.prototype.CGI = {
                login: "%s%s:%s/ISAPI/Security/userCheck",
                getAudioInfo: "%s%s:%s/ISAPI/System/TwoWayAudio/channels",
                getDeviceInfo: "%s%s:%s/ISAPI/System/deviceInfo",
                getAnalogChannelInfo: "%s%s:%s/ISAPI/System/Video/inputs/channels",
                getDigitalChannel: "%s%s:%s/ISAPI/ContentMgmt/InputProxy/channels",
                getDigitalChannelInfo: "%s%s:%s/ISAPI/ContentMgmt/InputProxy/channels/status",
                getZeroChannelInfo: "%s%s:%s/ISAPI/ContentMgmt/ZeroVideo/channels",
                getStreamChannels: {
                    analog: "%s%s:%s/ISAPI/Streaming/channels",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/StreamingProxy/channels"
                },
                getStreamDynChannels: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/DynStreaming/channels",
                startRealPlay: {
                    channels: "%s%s:%s/PSIA/streaming/channels/%s",
                    zeroChannels: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/ZeroStreaming/channels/%s"
                },
                startShttpRealPlay: {
                    channels: "%s%s:%s/SDK/play/%s/004",
                    zeroChannels: "%s%s:%s/SDK/play/100/004/ZeroStreaming"
                },
                startWsRealPlay: {channels: "%s%s:%s/%s", zeroChannels: "%s%s:%s/%s"},
                startVoiceTalk: {
                    open: "%s%s:%s/ISAPI/System/TwoWayAudio/channels/%s/open",
                    close: "%s%s:%s/ISAPI/System/TwoWayAudio/channels/%s/close",
                    audioData: "%s%s:%s/ISAPI/System/TwoWayAudio/channels/%s/audioData"
                },
                ptzControl: {
                    analog: "%s%s:%s/ISAPI/PTZCtrl/channels/%s/continuous",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/PTZCtrlProxy/channels/%s/continuous"
                },
                ptzAutoControl: {
                    ipdome: "%s%s:%s/ISAPI/PTZCtrl/channels/%s/presets/%s/goto",
                    analog: "%s%s:%s/ISAPI/PTZCtrl/channels/%s/autoPan",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/PTZCtrlProxy/channels/%s/autoPan"
                },
                setPreset: {
                    analog: "%s%s:%s/ISAPI/PTZCtrl/channels/%s/presets/%s",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/PTZCtrlProxy/channels/%s/presets/%s"
                },
                goPreset: {
                    analog: "%s%s:%s/ISAPI/PTZCtrl/channels/%s/presets/%s/goto",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/PTZCtrlProxy/channels/%s/presets/%s/goto"
                },
                ptzFocus: {
                    analog: "%s%s:%s/ISAPI/Image/channels/%s/focus",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/ImageProxy/channels/%s/focus",
                    ipc: "%s%s:%s/ISAPI/System/Video/inputs/channels/%s/focus"
                },
                ptzIris: {
                    analog: "%s%s:%s/ISAPI/Image/channels/%s/iris",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/ImageProxy/channels/%s/iris",
                    ipc: "%s%s:%s/ISAPI/System/Video/inputs/channels/%s/iris"
                },
                getNetworkBond: "%s%s:%s/ISAPI/System/Network/Bond",
                getNetworkInterface: "%s%s:%s/ISAPI/System/Network/interfaces",
                getUPnPPortStatus: "%s%s:%s/ISAPI/System/Network/UPnP/ports/status",
                getPPPoEStatus: "%s%s:%s/ISAPI/System/Network/PPPoE/1/status",
                getPortInfo: "%s%s:%s/ISAPI/Security/adminAccesses",
                recordSearch: "%s%s:%s/ISAPI/ContentMgmt/search",
                startPlayback: "%s%s:%s/PSIA/streaming/tracks/%s?starttime=%s&endtime=%s",
                startWsPlayback: "%s%s:%s/%s",
                startShttpPlayback: "%s%s:%s/SDK/playback/%s",
                startShttpReversePlayback: "%s%s:%s/SDK/playback/%s/reversePlay",
                startTransCodePlayback: "%s%s:%s/SDK/playback/%s/transcoding",
                startDownloadRecord: "%s%s:%s/ISAPI/ContentMgmt/download",
                downloaddeviceConfig: "%s%s:%s/ISAPI/System/configurationData",
                uploaddeviceConfig: "%s%s:%s/ISAPI/System/configurationData",
                restart: "%s%s:%s/ISAPI/System/reboot",
                restore: "%s%s:%s/ISAPI/System/factoryReset?mode=%s",
                startUpgrade: {
                    upgrade: "%s%s:%s/ISAPI/System/updateFirmware",
                    status: "%s%s:%s/ISAPI/System/upgradeStatus"
                },
                set3DZoom: {
                    analog: "%s%s:%s/ISAPI/PTZCtrl/channels/%s/position3D",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/PTZCtrlProxy/channels/%s/position3D"
                },
                getSecurityVersion: "%s%s:%s/ISAPI/Security/capabilities?username=admin",
                SDKCapabilities: "%s%s:%s/SDK/capabilities",
                deviceCapture: {channels: "%s%s:%s/ISAPI/Streaming/channels/%s/picture"},
                overlayInfo: {
                    analog: "%s%s:%s/ISAPI/System/Video/inputs/channels/%s/overlays/",
                    digital: "%s%s:%s/ISAPI/ContentMgmt/InputProxy/channels/%s/video/overlays"
                },
                sessionCap: "%s%s:%s/ISAPI/Security/sessionLogin/capabilities?username=%s",
                sessionLogin: "%s%s:%s/ISAPI/Security/sessionLogin",
                sessionHeartbeat: "%s%s:%s/ISAPI/Security/sessionHeartbeat",
                sessionLogout: "%s%s:%s/ISAPI/Security/sessionLogout",
                systemCapabilities: "%s%s:%s/ISAPI/System/capabilities"
            }, yt.prototype.login = function (e, t, n, r) {
                var i = 2 == r.protocol ? "https://" : "http://", o = X(this.CGI.login, i, e, t), s = new mt,
                    a = {type: "GET", url: o, auth: n, success: null, error: null};
                S.extend(a, r), S.extend(a, {
                    success: function (e) {
                        r.success && r.success(e)
                    }, error: function (e, t) {
                        r.error && r.error(e, t)
                    }
                }), s.setRequestParam(a), s.submitRequest()
            }, yt.prototype.getAudioInfo = function (e, t) {
                var n = X(this.CGI.getAudioInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getSecurityVersion = function (e, t) {
                var n = X(this.CGI.getSecurityVersion, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getDeviceInfo = function (e, n) {
                var r = X(this.CGI.getDeviceInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        var r = [];
                        r.push("<DeviceInfo>"), r.push("<deviceName>" + S.escape(t.$XML(e).find("deviceName").eq(0).text()) + "</deviceName>"), r.push("<deviceID>" + t.$XML(e).find("deviceID").eq(0).text() + "</deviceID>"), r.push("<deviceType>" + t.$XML(e).find("deviceType").eq(0).text() + "</deviceType>"), r.push("<model>" + t.$XML(e).find("model").eq(0).text() + "</model>"), r.push("<serialNumber>" + t.$XML(e).find("serialNumber").eq(0).text() + "</serialNumber>"), r.push("<macAddress>" + t.$XML(e).find("macAddress").eq(0).text() + "</macAddress>"), r.push("<firmwareVersion>" + t.$XML(e).find("firmwareVersion").eq(0).text() + "</firmwareVersion>"), r.push("<firmwareReleasedDate>" + t.$XML(e).find("firmwareReleasedDate").eq(0).text() + "</firmwareReleasedDate>"), r.push("<encoderVersion>" + t.$XML(e).find("encoderVersion").eq(0).text() + "</encoderVersion>"), r.push("<encoderReleasedDate>" + t.$XML(e).find("encoderReleasedDate").eq(0).text() + "</encoderReleasedDate>"), r.push("</DeviceInfo>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, yt.prototype.getAnalogChannelInfo = function (e, n) {
                var r = X(this.CGI.getAnalogChannelInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (r) {
                        var i = [];
                        i.push("<VideoInputChannelList>");
                        var o = t.$XML(r).find("VideoInputChannel", !0);
                        e.iAnalogChannelNum = o.length;
                        for (var s = 0, a = o.length; a > s; s++) {
                            var u = o[s];
                            i.push("<VideoInputChannel>"), i.push("<id>" + t.$XML(u).find("id").eq(0).text() + "</id>"), i.push("<inputPort>" + t.$XML(u).find("inputPort").eq(0).text() + "</inputPort>"), i.push("<name>" + S.escape(t.$XML(u).find("name").eq(0).text()) + "</name>"), i.push("<videoFormat>" + t.$XML(u).find("videoFormat").eq(0).text() + "</videoFormat>"), i.push("</VideoInputChannel>")
                        }
                        i.push("</VideoInputChannelList>"), r = S.loadXML(i.join("")), n.success && n.success(r)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, yt.prototype.getDigitalChannel = function (e, n) {
                var r = X(this.CGI.getDigitalChannel, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        var r = [];
                        r.push("<InputProxyChannelList>");
                        for (var i = t.$XML(e).find("InputProxyChannel", !0), o = 0, s = i.length; s > o; o++) {
                            var a = i[o];
                            r.push("<InputProxyChannel>"), r.push("<id>" + t.$XML(a).find("id").eq(0).text() + "</id>"), r.push("<name>" + S.escape(t.$XML(a).find("name").eq(0).text()) + "</name>"), r.push("</InputProxyChannel>")
                        }
                        r.push("</InputProxyChannelList>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, yt.prototype.getDigitalChannelInfo = function (e, n) {
                var r = null, i = {};
                if (this.getDigitalChannel(e, {
                    async: !1, success: function (e) {
                        r = e;
                        for (var n = t.$XML(r).find("InputProxyChannel", !0), o = 0, s = n.length; s > o; o++) {
                            var a = n[o], u = t.$XML(a).find("id").eq(0).text(),
                                l = t.$XML(a).find("name").eq(0).text();
                            i[u] = l
                        }
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), null !== r) {
                    var o = X(this.CGI.getDigitalChannelInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), s = new mt,
                        a = {type: "GET", url: o, auth: e.szAuth, success: null, error: null};
                    S.extend(a, n), S.extend(a, {
                        success: function (e) {
                            var r = [];
                            r.push("<InputProxyChannelStatusList>");
                            for (var o = t.$XML(e).find("InputProxyChannelStatus", !0), s = 0, a = o.length; a > s; s++) {
                                var u = o[s], l = t.$XML(u).find("id").eq(0).text();
                                r.push("<InputProxyChannelStatus>"), r.push("<id>" + l + "</id>"), r.push("<sourceInputPortDescriptor>"), r.push("<proxyProtocol>" + t.$XML(u).find("proxyProtocol").eq(0).text() + "</proxyProtocol>"), r.push("<addressingFormatType>" + t.$XML(u).find("addressingFormatType").eq(0).text() + "</addressingFormatType>"), r.push("<ipAddress>" + t.$XML(u).find("ipAddress").eq(0).text() + "</ipAddress>"), r.push("<managePortNo>" + t.$XML(u).find("managePortNo").eq(0).text() + "</managePortNo>"), r.push("<srcInputPort>" + t.$XML(u).find("srcInputPort").eq(0).text() + "</srcInputPort>"), r.push("<userName>" + S.escape(t.$XML(u).find("userName").eq(0).text()) + "</userName>"), r.push("<streamType>" + t.$XML(u).find("streamType").eq(0).text() + "</streamType>"), r.push("<online>" + t.$XML(u).find("online").eq(0).text() + "</online>"), r.push("<name>" + S.escape(i[l]) + "</name>"), r.push("</sourceInputPortDescriptor>"), r.push("</InputProxyChannelStatus>")
                            }
                            r.push("</InputProxyChannelStatusList>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                        }, error: function (e, t) {
                            n.error && n.error(e, t)
                        }
                    }), s.setRequestParam(a), s.submitRequest()
                }
            }, yt.prototype.getZeroChannelInfo = function (e, t) {
                var n = X(this.CGI.getZeroChannelInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getStreamChannels = function (e, t) {
                if (0 != e.iAnalogChannelNum) var n = X(this.CGI.getStreamChannels.analog, e.szHttpProtocol, e.szIP, e.iCGIPort); else var n = X(this.CGI.getStreamChannels.digital, e.szHttpProtocol, e.szIP, e.iCGIPort);
                var r = new mt, i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getPPPoEStatus = function (e, t) {
                var n = X(this.CGI.getPPPoEStatus, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getUPnPPortStatus = function (e, t) {
                var n = X(this.CGI.getUPnPPortStatus, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getNetworkBond = function (e, t) {
                var n = X(this.CGI.getNetworkBond, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getNetworkInterface = function (e, t) {
                var n = X(this.CGI.getNetworkInterface, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getPortInfo = function (e, t) {
                var n = X(this.CGI.getPortInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.startRealPlay = function (e, t) {
                var n = 100 * t.iChannelID + t.iStreamType, r = "", i = -1, o = e.szIP;
                if ("rtsp://" === t.urlProtocol && (o = U(o)), t.bZeroChannel ? (lt() && (n = 0), r = X(t.cgi.zeroChannels, t.urlProtocol, o, t.iPort, n)) : r = X(t.cgi.channels, t.urlProtocol, o, t.iPort, n), d.proxyAddress && lt()) {
                    S.cookie("webVideoCtrlProxy", o + ":" + t.iPort, {raw: !0}), r = X(t.cgi.zeroChannels, t.urlProtocol, d.proxyAddress.ip, d.proxyAddress.port, n);
                    var s = o + ":" + t.iPort;
                    r += r.indexOf("?") > -1 ? "&deviceIdentify=" + s : "?deviceIdentify=" + s
                }
                var a = function () {
                    var n = new pt;
                    n.iIndex = t.iWndIndex, n.szIP = e.szIP, n.iCGIPort = e.iCGIPort, n.szDeviceIdentify = e.szDeviceIdentify, n.iChannelID = t.iChannelID, n.iPlayStatus = _, y.push(n)
                }, u = $.Deferred();
                if (lt()) {
                    var l = {sessionID: e.szAuth, token: Z(e, e.szDeviceIdentify)};
                    r = dt(r), f.JS_Play(r, l, t.iWndIndex).then(function () {
                        a(), u.resolve()
                    }, function () {
                        u.reject()
                    })
                } else i = f.HWP_Play(r, e.szAuth, t.iWndIndex, "", ""), 0 == i ? (a(), u.resolve()) : u.reject();
                return u
            }, yt.prototype.startVoiceTalk = function (e, t) {
                var n = X(this.CGI.startVoiceTalk.open, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    r = X(this.CGI.startVoiceTalk.close, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    i = X(this.CGI.startVoiceTalk.audioData, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    o = f.HWP_StartVoiceTalkEx(n, r, i, e.szAuth, e.iAudioType, e.m_iAudioBitRate, e.m_iAudioSamplingRate);
                return o
            }, yt.prototype.ptzAutoControl = function (e, t, n, r) {
                var i = n.iChannelID, o = "", s = "";
                if (r.iPTZSpeed = r.iPTZSpeed < 7 ? 15 * r.iPTZSpeed : 100, t && (r.iPTZSpeed = 0), e.szDeviceType != H) o = i <= e.iAnalogChannelNum ? X(this.CGI.ptzAutoControl.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID) : n.bShttpIPChannel ? X(this.CGI.ptzAutoControl.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID - e.oStreamCapa.iIpChanBase + 1 + e.iAnalogChannelNum) : X(this.CGI.ptzAutoControl.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID), s = "<?xml version='1.0' encoding='UTF-8'?><autoPanData><autoPan>" + r.iPTZSpeed + "</autoPan>" + "</autoPanData>"; else {
                    0 === r.iPTZSpeed && (t = !0);
                    var a = 99;
                    t && (a = 96), o = X(this.CGI.ptzAutoControl.ipdome, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID, a)
                }
                var u = new mt,
                    l = {type: "PUT", url: o, async: !1, auth: e.szAuth, data: s, success: null, error: null}, c = this;
                S.extend(l, r), S.extend(l, {
                    success: function (e) {
                        n.bPTZAuto = !n.bPTZAuto, r.success && r.success(e)
                    }, error: function (t, i) {
                        if (W == e.szDeviceType || N == e.szDeviceType) {
                            o = n.bShttpIPChannel ? X(c.CGI.ptzControl.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID - e.oStreamCapa.iIpChanBase + 1 + e.iAnalogChannelNum) : X(c.CGI.ptzControl.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID), s = "<?xml version='1.0' encoding='UTF-8'?><PTZData><pan>" + r.iPTZSpeed + "</pan>" + "<tilt>" + 0 + "</tilt>" + "</PTZData>";
                            var a = new mt, u = {
                                type: "PUT",
                                url: o,
                                async: !1,
                                auth: e.szAuth,
                                data: s,
                                success: null,
                                error: null
                            };
                            S.extend(u, r), a.setRequestParam(u), a.submitRequest()
                        } else r.error && r.error(t, i)
                    }
                }), u.setRequestParam(l), u.submitRequest()
            }, yt.prototype.ptzControl = function (e, t, n, r) {
                var i = n.iChannelID, o = "";
                n.bPTZAuto && this.ptzAutoControl(e, !0, n, {iPTZSpeed: 0}), r.iPTZSpeed = t ? 0 : r.iPTZSpeed < 7 ? 15 * r.iPTZSpeed : 100;
                var s = [{}, {pan: 0, tilt: r.iPTZSpeed}, {pan: 0, tilt: -r.iPTZSpeed}, {
                        pan: -r.iPTZSpeed,
                        tilt: 0
                    }, {pan: r.iPTZSpeed, tilt: 0}, {pan: -r.iPTZSpeed, tilt: r.iPTZSpeed}, {
                        pan: -r.iPTZSpeed,
                        tilt: -r.iPTZSpeed
                    }, {pan: r.iPTZSpeed, tilt: r.iPTZSpeed}, {
                        pan: r.iPTZSpeed,
                        tilt: -r.iPTZSpeed
                    }, {}, {speed: r.iPTZSpeed}, {speed: -r.iPTZSpeed}, {speed: r.iPTZSpeed}, {speed: -r.iPTZSpeed}, {speed: r.iPTZSpeed}, {speed: -r.iPTZSpeed}],
                    a = "", u = {};
                switch (r.iPTZIndex) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        u = this.CGI.ptzControl, a = "<?xml version='1.0' encoding='UTF-8'?><PTZData><pan>" + s[r.iPTZIndex].pan + "</pan>" + "<tilt>" + s[r.iPTZIndex].tilt + "</tilt>" + "</PTZData>";
                        break;
                    case 10:
                    case 11:
                        u = this.CGI.ptzControl, a = "<?xml version='1.0' encoding='UTF-8'?><PTZData><zoom>" + s[r.iPTZIndex].speed + "</zoom>" + "</PTZData>";
                        break;
                    case 12:
                    case 13:
                        u = this.CGI.ptzFocus, a = "<?xml version='1.0' encoding='UTF-8'?><FocusData><focus>" + s[r.iPTZIndex].speed + "</focus>" + "</FocusData>";
                        break;
                    case 14:
                    case 15:
                        u = this.CGI.ptzIris, a = "<?xml version='1.0' encoding='UTF-8'?><IrisData><iris>" + s[r.iPTZIndex].speed + "</iris>" + "</IrisData>";
                        break;
                    default:
                        return r.error && r.error(), void 0
                }
                o = u != this.CGI.ptzFocus && u != this.CGI.ptzIris || e.szDeviceType != W && e.szDeviceType != H && e.szDeviceType != N ? i <= e.iAnalogChannelNum ? X(u.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID) : n.bShttpIPChannel ? X(u.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID - e.oStreamCapa.iIpChanBase + 1 + e.iAnalogChannelNum) : X(u.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID) : X(u.ipc, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID);
                var l = new mt,
                    c = {type: "PUT", url: o, async: !1, auth: e.szAuth, data: a, success: null, error: null};
                S.extend(c, r), S.extend(c, {
                    success: function (e) {
                        r.success && r.success(e)
                    }, error: function (e, t) {
                        r.error && r.error(e, t)
                    }
                }), l.setRequestParam(c), l.submitRequest()
            }, yt.prototype.setPreset = function (e, t, n) {
                var r = t.iChannelID, i = "", o = "";
                i = r <= e.iAnalogChannelNum ? X(this.CGI.setPreset.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID, n.iPresetID) : t.bShttpIPChannel ? X(this.CGI.setPreset.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID - e.oStreamCapa.iIpChanBase + 1 + e.iAnalogChannelNum, n.iPresetID) : X(this.CGI.setPreset.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID, n.iPresetID), o = "<?xml version='1.0' encoding='UTF-8'?>", o += "<PTZPreset>", o += "<id>" + n.iPresetID + "</id>", e.szDeviceType != H && (o += "<presetName>Preset" + n.iPresetID + "</presetName>"), o += "</PTZPreset>";
                var s = new mt, a = {type: "PUT", url: i, auth: e.szAuth, data: o, success: null, error: null};
                S.extend(a, n), S.extend(a, {
                    success: function (e) {
                        n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), s.setRequestParam(a), s.submitRequest()
            }, yt.prototype.goPreset = function (e, t, n) {
                var r = t.iChannelID, i = "";
                i = r <= e.iAnalogChannelNum ? X(this.CGI.goPreset.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID, n.iPresetID) : t.bShttpIPChannel ? X(this.CGI.goPreset.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID - e.oStreamCapa.iIpChanBase + 1 + e.iAnalogChannelNum, n.iPresetID) : X(this.CGI.goPreset.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID, n.iPresetID);
                var o = new mt, s = {type: "PUT", url: i, auth: e.szAuth, success: null, error: null};
                S.extend(s, n), S.extend(s, {
                    success: function (e) {
                        n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), o.setRequestParam(s), o.submitRequest()
            }, yt.prototype.overlayInfo = function () {
                return szUrl = this.CGI.overlayInfo.analog
            }, yt.prototype.recordSearch = function (e, n) {
                var r = "", i = "", o = n.iChannelID, a = n.iStreamType, u = n.szStartTime.replace(" ", "T") + "Z",
                    l = n.szEndTime.replace(" ", "T") + "Z";
                r = X(this.CGI.recordSearch, e.szHttpProtocol, e.szIP, e.iCGIPort), i = "<?xml version='1.0' encoding='UTF-8'?><CMSearchDescription><searchID>" + new s + "</searchID>" + "<trackList><trackID>" + (100 * o + a) + "</trackID></trackList>" + "<timeSpanList>" + "<timeSpan>" + "<startTime>" + u + "</startTime>" + "<endTime>" + l + "</endTime>" + "</timeSpan>" + "</timeSpanList>" + "<maxResults>40</maxResults>" + "<searchResultPostion>" + n.iSearchPos + "</searchResultPostion>" + "<metadataList>" + "<metadataDescriptor>//metadata.ISAPI.org/VideoMotion</metadataDescriptor>" + "</metadataList>" + "</CMSearchDescription>";
                var c = new mt, d = {type: "POST", url: r, auth: e.szAuth, data: i, success: null, error: null};
                S.extend(d, n), S.extend(d, {
                    success: function (e) {
                        var r = [];
                        r.push("<CMSearchResult>"), r.push("<responseStatus>" + t.$XML(e).find("responseStatus").eq(0).text() + "</responseStatus>"), r.push("<responseStatusStrg>" + t.$XML(e).find("responseStatusStrg").eq(0).text() + "</responseStatusStrg>"), r.push("<numOfMatches>" + t.$XML(e).find("numOfMatches").eq(0).text() + "</numOfMatches>"), r.push("<matchList>");
                        for (var i = t.$XML(e).find("searchMatchItem", !0), o = 0, s = i.length; s > o; o++) {
                            var a = i[o];
                            r.push("<searchMatchItem>"), r.push("<trackID>" + t.$XML(a).find("trackID").eq(0).text() + "</trackID>"), r.push("<startTime>" + t.$XML(a).find("startTime").eq(0).text() + "</startTime>"), r.push("<endTime>" + t.$XML(a).find("endTime").eq(0).text() + "</endTime>"), r.push("<playbackURI>" + S.escape(t.$XML(a).find("playbackURI").eq(0).text()) + "</playbackURI>"), r.push("<metadataDescriptor>" + t.$XML(a).find("metadataDescriptor").eq(0).text().split("/")[1] + "</metadataDescriptor>"), r.push("</searchMatchItem>")
                        }
                        r.push("</matchList>"), r.push("</CMSearchResult>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), c.setRequestParam(d), c.submitRequest()
            }, yt.prototype.startPlayback = function (e, t) {
                var n = t.iWndIndex, r = "", i = t.szStartTime, o = t.szEndTime, s = e.szIP;
                if ("rtsp://" === t.urlProtocol && (s = U(s)), lt()) if (d.proxyAddress) {
                    S.cookie("webVideoCtrlProxy", s + ":" + t.iPort, {raw: !0}), r = X(t.cgi, t.urlProtocol, d.proxyAddress.ip, d.proxyAddress.port, t.iChannelID);
                    var a = s + ":" + t.iPort;
                    r += r.indexOf("?") > -1 ? "&deviceIdentify=" + a : "?deviceIdentify=" + a
                } else r = X(t.cgi, t.urlProtocol, s, t.iPort, t.iChannelID, i, o); else r = X(t.cgi, t.urlProtocol, s, t.iPort, t.iChannelID, i, o);
                if (!j(t.oTransCodeParam)) {
                    var u = ut(t.oTransCodeParam);
                    if ("" == u) return -1;
                    f.HWP_SetTrsPlayBackParam(n, u)
                }
                var l = function () {
                    var r = new pt;
                    r.iIndex = n, r.szIP = e.szIP, r.iCGIPort = e.iCGIPort, r.szDeviceIdentify = e.szDeviceIdentify, r.iChannelID = t.iChannelID, r.iPlayStatus = D, y.push(r)
                }, c = $.Deferred();
                if (lt()) {
                    var h = {sessionID: e.szAuth, token: Z(e, e.szDeviceIdentify)};
                    f.JS_Play(r, h, n, i, o).then(function () {
                        l(), c.resolve()
                    }, function () {
                        c.reject()
                    })
                } else {
                    var p = f.HWP_Play(r, e.szAuth, n, i, o);
                    0 == p ? (l(), c.resolve()) : c.reject()
                }
                return c
            }, yt.prototype.reversePlayback = function (e, t) {
                var n = t.iWndIndex, r = t.szStartTime, i = t.szEndTime, o = e.szIP;
                "rtsp://" === t.urlProtocol && (o = U(o));
                var s = X(t.cgi, t.urlProtocol, o, t.iPort, t.iChannelID, r, i),
                    a = f.HWP_ReversePlay(s, e.szAuth, n, r, i);
                if (0 == a) {
                    var u = new pt;
                    u.iIndex = n, u.szIP = e.szIP, u.iCGIPort = e.iCGIPort, u.szDeviceIdentify = e.szDeviceIdentify, u.iChannelID = t.iChannelID, u.iPlayStatus = z, y.push(u)
                }
                return a
            }, yt.prototype.startDownloadRecord = function (e, t) {
                var n = -1, r = X(this.CGI.startDownloadRecord, e.szHttpProtocol, e.szIP, e.iCGIPort),
                    i = "<?xml version='1.0' encoding='UTF-8'?><downloadRequest><playbackURI> " + S.escape(t.szPlaybackURI) + "</playbackURI>" + "</downloadRequest>";
                return lt() ? (r = ct(r), n = f.JS_StartDownload(r, e.szAuth, t.szFileName, i)) : n = f.HWP_StartDownload(r, e.szAuth, t.szFileName, i, t.bDateDir), n
            }, yt.prototype.exportDeviceConfig = function (e, t) {
                var n = X(this.CGI.downloaddeviceConfig, e.szHttpProtocol, e.szIP, e.iCGIPort);
                if (lt()) {
                    var r = {
                        type: "PUT", url: n, auth: e.szAuth, success: function () {
                        }, error: function () {
                        }
                    }, i = new mt;
                    return i.setRequestParam(r), i.submitRequest(), n = ct(n), t && (n = S.exportPasswordDeviceConfig(n, t)), f.JS_ExportDeviceConfig(n)
                }
                return t && (n = S.exportPasswordDeviceConfig(n, t)), f.HWP_ExportDeviceConfig(n, e.szAuth, "", 0)
            }, yt.prototype.importDeviceConfig = function (e, t, n) {
                var r = X(this.CGI.uploaddeviceConfig, e.szHttpProtocol, e.szIP, e.iCGIPort);
                if (lt()) {
                    var i = {
                        type: "PUT", url: r, auth: e.szAuth, success: function () {
                        }, error: function () {
                        }
                    }, o = new mt;
                    return o.setRequestParam(i), o.submitRequest(), r = ct(r), n && (r = S.exportPasswordDeviceConfig(r, n)), f.JS_UploadFile(r)
                }
                return n && (r = S.exportPasswordDeviceConfig(r, n)), f.HWP_ImportDeviceConfig(r, e.szAuth, t.szFileName, 0)
            }, yt.prototype.restart = function (e, t) {
                var n = X(this.CGI.restart, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "PUT", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.restore = function (e, t, n) {
                var r = X(this.CGI.restore, e.szHttpProtocol, e.szIP, e.iCGIPort, t), i = new mt,
                    o = {type: "PUT", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, yt.prototype.startUpgrade = function (e, t) {
                $.Deferred();
                var n = X(this.CGI.startUpgrade.upgrade, e.szHttpProtocol, e.szIP, e.iCGIPort),
                    r = X(this.CGI.startUpgrade.status, e.szHttpProtocol, e.szIP, e.iCGIPort);
                return szRet = f.HWP_StartUpgrade(n, r, e.szAuth, t.szFileName)
            }, yt.prototype.asyncstartUpgrade = function (e, t) {
                var n = $.Deferred(), r = X(this.CGI.startUpgrade.upgrade, e.szHttpProtocol, e.szIP, e.iCGIPort),
                    i = X(this.CGI.startUpgrade.status, e.szHttpProtocol, e.szIP, e.iCGIPort);
                if (lt()) r = ct(r), i = ct(i), f.JS_StartUpgradeEx(r, i).then(function (e) {
                    n.resolve(e)
                }, function () {
                    n.reject(o)
                }); else {
                    var o = f.HWP_StartUpgrade(r, i, e.szAuth, t.szFileName);
                    0 === o ? n.resolve(o) : n.reject(o)
                }
                return n
            }, yt.prototype.set3DZoom = function (e, n, r, i) {
                var o = n.iChannelID, s = "";
                if (s = o <= e.iAnalogChannelNum ? X(this.CGI.set3DZoom.analog, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID) : n.bShttpIPChannel ? X(this.CGI.set3DZoom.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID - e.oStreamCapa.iIpChanBase + 1 + e.iAnalogChannelNum) : X(this.CGI.set3DZoom.digital, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID), lt()) var l = S.loadXML(r),
                    c = parseInt(255 * (r.startPos[0] / a), 10), d = parseInt(255 * (r.startPos[1] / u), 10),
                    f = parseInt(255 * (r.endPos[0] / a), 10),
                    h = parseInt(255 * (r.endPos[1] / u), 10); else var l = S.loadXML(r),
                    c = parseInt(t.$XML(l).find("StartPoint").eq(0).find("positionX").eq(0).text(), 10),
                    d = parseInt(t.$XML(l).find("StartPoint").eq(0).find("positionY").eq(0).text(), 10),
                    f = parseInt(t.$XML(l).find("EndPoint").eq(0).find("positionX").eq(0).text(), 10),
                    h = parseInt(t.$XML(l).find("EndPoint").eq(0).find("positionY").eq(0).text(), 10);
                var p = "<?xml version='1.0' encoding='UTF-8'?><Position3D><StartPoint><positionX>" + c + "</positionX>" + "<positionY>" + (255 - d) + "</positionY>" + "</StartPoint>" + "<EndPoint>" + "<positionX>" + f + "</positionX>" + "<positionY>" + (255 - h) + "</positionY>" + "</EndPoint>" + "</Position3D>",
                    m = new mt, y = {type: "PUT", url: s, data: p, auth: e.szAuth, success: null, error: null};
                S.extend(y, i), S.extend(y, {
                    success: function (e) {
                        i.success && i.success(e)
                    }, error: function (e, t) {
                        i.error && i.error(e, t)
                    }
                }), m.setRequestParam(y), m.submitRequest()
            }, yt.prototype.getSDKCapa = function (e, t) {
                var n = X(this.CGI.SDKCapabilities, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, async: !1, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.deviceCapturePic = function (e, t, n, r) {
                var t = 100 * t + 1, i = -1,
                    o = X(this.CGI.deviceCapture.channels, e.szHttpProtocol, e.szIP, e.iCGIPort, t), s = [];
                if (S.isInt(r.iResolutionWidth) && s.push("videoResolutionWidth=" + r.iResolutionWidth), S.isInt(r.iResolutionHeight) && s.push("videoResolutionHeight=" + r.iResolutionHeight), s.length > 0 && (o += "?" + s.join("&")), lt()) {
                    var a = function (e, t) {
                        var n = ".jpg";
                        return $("body").append('<a id="jsplugin_download_a" href="' + e + '" download=' + t + n + '><li id="jsplugin_download_li"></li></a>'), $("#jsplugin_download_li").trigger("click"), $("#jsplugin_download_a").remove(), 0
                    };
                    o = ct(o), i = a(o, n)
                } else i = f.HWP_DeviceCapturePic(o, e.szAuth, n, r.bDateDir);
                return i
            }, yt.prototype.getSessionV2Cap = function (e, t, n, r, i, o) {
                var s = "";
                s = 2 == n ? "https://" : "http://";
                var a = X(this.CGI.sessionCap, s, t, r, encodeURIComponent(i));
                a = a + "&random:" + e;
                var u = new mt, l = {type: "GET", url: a, auth: "", success: null, error: null};
                S.extend(l, o), S.extend(l, {
                    success: function (e) {
                        o.success && o.success(e)
                    }, error: function (e, t) {
                        o.error && o.error(e, t)
                    }
                }), u.setRequestParam(l), u.submitRequest()
            }, yt.prototype.getSessionCap = function (e, t, n, r, i) {
                var o = "";
                o = 2 == t ? "https://" : "http://";
                var s = X(this.CGI.sessionCap, o, e, n, encodeURIComponent(r)), a = new mt,
                    u = {type: "GET", url: s, auth: "", success: null, error: null};
                S.extend(u, i), S.extend(u, {
                    success: function (e) {
                        i.success && i.success(e)
                    }, error: function (e, t) {
                        i.error && i.error(e, t)
                    }
                }),
                    a.setRequestParam(u),
                    a.submitRequest()
            }, yt.prototype.sessionV2Login = function (e, n, r, i, o, s, a, u) {
                var l = "";
                l = 2 == r ? "https://" : "http://";
                var c = parseInt(t.$XML(a).find("sessionIDVersion").eq(0).text(), 10),
                    d = "true" === t.$XML(a).find("isSessionIDValidLongTerm").eq(0).text(),
                    f = X(this.CGI.sessionLogin, l, n, i), h = t.$XML(a).find("sessionID").eq(0).text(),
                    p = t.$XML(a).find("challenge").eq(0).text(),
                    m = parseInt(t.$XML(a).find("iterations").eq(0).text(), 10), y = !1, v = "";
                t.$XML(a).find("isIrreversible", !0).length > 0 && (y = "true" === t.$XML(a).find("isIrreversible").eq(0).text(), v = t.$XML(a).find("salt").eq(0).text()), this.m_oInfoForLocalPlgin = {
                    szRandom: e,
                    sessionID: h,
                    iterations: m,
                    challenge: p,
                    user: o
                };
                var g = S.encodePwd(s, {challenge: p, userName: o, salt: v, iIterate: m}, y), P = "<SessionLogin>";
                P += "<userName>" + S.escape(o) + "</userName>", P += "<password>" + g + "</password>", P += "<sessionID>" + h + "</sessionID>", P += "<isSessionIDValidLongTerm>" + d + "</isSessionIDValidLongTerm>", P += "<sessionIDVersion>" + c + "</sessionIDVersion>", P += "</SessionLogin>";
                var I = new mt, C = {type: "POST", url: f, data: P, auth: "", success: null, error: null};
                S.extend(C, u), S.extend(C, {
                    success: function (e) {
                        u.success && u.success(e)
                    }, error: function (e, t) {
                        u.error && u.error(e, t)
                    }
                }), I.setRequestParam(C), I.submitRequest()
            }, yt.prototype.sessionLogin = function (e, n, r, i, o, s, a) {
                var u = "";
                u = 2 == n ? "https://" : "http://";
                var l = X(this.CGI.sessionLogin, u, e, r), c = t.$XML(s).find("sessionID").eq(0).text(),
                    d = t.$XML(s).find("challenge").eq(0).text(),
                    f = parseInt(t.$XML(s).find("iterations").eq(0).text(), 10), h = !1, p = "";
                t.$XML(s).find("isIrreversible", !0).length > 0 && (h = "true" === t.$XML(s).find("isIrreversible").eq(0).text(), p = t.$XML(s).find("salt").eq(0).text());
                var m = "";
                if (h) {
                    m = S.sha256(i + p + o), m = S.sha256(m + d);
                    for (var y = 2; f > y; y++) m = S.sha256(m)
                } else {
                    m = S.sha256(o) + d;
                    for (var y = 1; f > y; y++) m = S.sha256(m)
                }
                var v = "<SessionLogin>";
                v += "<userName>" + S.escape(i) + "</userName>", v += "<password>" + m + "</password>", v += "<sessionID>" + c + "</sessionID>", v += "</SessionLogin>";
                var g = new mt, P = {type: "POST", url: l, data: v, auth: "", success: null, error: null};
                S.extend(P, a), S.extend(P, {
                    success: function (e) {
                        a.success && a.success(e)
                    }, error: function (e, t) {
                        a.error && a.error(e, t)
                    }
                }), g.setRequestParam(P), g.submitRequest()
            }, yt.prototype.sessionHeartbeat = function (e, t, n) {
                var r = X(this.CGI.sessionHeartbeat, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "PUT", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, {
                    success: function (e) {
                        t && t(e)
                    }, error: function (e, t) {
                        n && n(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, yt.prototype.sessionLogout = function (e, t) {
                var n = X(this.CGI.sessionLogout, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "PUT", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, yt.prototype.getSystemCapa = function (e, t) {
                var n = X(this.CGI.systemCapabilities, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, async: !1, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            };
            var vt = function () {
            };
            vt.prototype.CGI = {
                login: "%s%s:%s/PSIA/Custom/SelfExt/userCheck",
                getAudioInfo: "%s%s:%s/PSIA/Custom/SelfExt/TwoWayAudio/channels",
                getDeviceInfo: "%s%s:%s/PSIA/System/deviceInfo",
                getAnalogChannelInfo: "%s%s:%s/PSIA/System/Video/inputs/channels",
                getDigitalChannel: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/DynVideo/inputs/channels",
                getDigitalChannelInfo: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/DynVideo/inputs/channels/status",
                getZeroChannelInfo: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/ZeroVideo/channels",
                getStreamChannels: {
                    analog: "%s%s:%s/PSIA/Streaming/channels",
                    digital: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/DynStreaming/channels"
                },
                getStreamDynChannels: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/DynStreaming/channels",
                startRealPlay: {
                    channels: "%s%s:%s/PSIA/streaming/channels/%s",
                    zeroChannels: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/ZeroStreaming/channels/%s"
                },
                startVoiceTalk: {
                    open: "%s%s:%s/PSIA/Custom/SelfExt/TwoWayAudio/channels/%s/open",
                    close: "%s%s:%s/PSIA/Custom/SelfExt/TwoWayAudio/channels/%s/close",
                    audioData: "%s%s:%s/PSIA/Custom/SelfExt/TwoWayAudio/channels/%s/audioData"
                },
                ptzControl: "%s%s:%s/PSIA/PTZ/channels/%s/continuous",
                ptzAutoControl: "%s%s:%s/PSIA/Custom/SelfExt/PTZ/channels/%s/autoptz",
                setPreset: "%s%s:%s/PSIA/PTZ/channels/%s/presets/%s",
                goPreset: "%s%s:%s/PSIA/PTZ/channels/%s/presets/%s/goto",
                ptzFocus: "%s%s:%s/PSIA/System/Video/inputs/channels/%s/focus",
                ptzIris: "%s%s:%s/PSIA/System/Video/inputs/channels/%s/iris",
                getNetworkBond: "%s%s:%s/PSIA/Custom/SelfExt/Bond",
                getNetworkInterface: "%s%s:%s/PSIA/System/Network/interfaces",
                getUPnPPortStatus: "%s%s:%s/PSIA/Custom/SelfExt/UPnP/ports/status",
                getPPPoEStatus: "%s%s:%s/PSIA/Custom/SelfExt/PPPoE/1/status",
                getPortInfo: "%s%s:%s/PSIA/Security/AAA/adminAccesses",
                recordSearch: "%s%s:%s/PSIA/ContentMgmt/search",
                startPlayback: "%s%s:%s/PSIA/streaming/tracks/%s?starttime=%s&endtime=%s",
                startDownloadRecord: "%s%s:%s/PSIA/Custom/SelfExt/ContentMgmt/download",
                deviceConfig: "%s%s:%s/PSIA/System/configurationData",
                restart: "%s%s:%s/PSIA/System/reboot",
                restore: "%s%s:%s/PSIA/System/factoryReset?mode=%s",
                startUpgrade: {
                    upgrade: "%s%s:%s/PSIA/System/updateFirmware",
                    status: "%s%s:%s/PSIA/Custom/SelfExt/upgradeStatus"
                },
                set3DZoom: "%s%s:%s/PSIA/Custom/SelfExt/PTZ/channels/%s/Set3DZoom",
                deviceCapture: {channels: "%s%s:%s/PSIA/Streaming/channels/%s/picture"},
                systemCapabilities: "%s%s:%s/PSIA/System/capabilities"
            }, vt.prototype.login = function (e, n, r, i) {
                var o = 2 == i.protocol ? "https://" : "http://", s = X(this.CGI.login, o, e, n), a = new mt,
                    u = {type: "GET", url: s, auth: r, success: null, error: null};
                S.extend(u, i), S.extend(u, {
                    success: function (e) {
                        "200" == t.$XML(e).find("statusValue").eq(0).text() ? i.success && i.success(e) : i.error && i.error(401, e)
                    }, error: function (e, t) {
                        i.error && i.error(e, t)
                    }
                }), a.setRequestParam(u), a.submitRequest()
            }, vt.prototype.getAudioInfo = function (e, t) {
                var n = X(this.CGI.getAudioInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getDeviceInfo = function (e, n) {
                var r = X(this.CGI.getDeviceInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        var r = [];
                        r.push("<DeviceInfo>"), r.push("<deviceName>" + S.escape(t.$XML(e).find("deviceName").eq(0).text()) + "</deviceName>"), r.push("<deviceID>" + t.$XML(e).find("deviceID").eq(0).text() + "</deviceID>"), r.push("<deviceType>" + t.$XML(e).find("deviceDescription").eq(0).text() + "</deviceType>"), r.push("<model>" + t.$XML(e).find("model").eq(0).text() + "</model>"), r.push("<serialNumber>" + t.$XML(e).find("serialNumber").eq(0).text() + "</serialNumber>"), r.push("<macAddress>" + t.$XML(e).find("macAddress").eq(0).text() + "</macAddress>"), r.push("<firmwareVersion>" + t.$XML(e).find("firmwareVersion").eq(0).text() + "</firmwareVersion>"), r.push("<firmwareReleasedDate>" + t.$XML(e).find("firmwareReleasedDate").eq(0).text() + "</firmwareReleasedDate>"), r.push("<encoderVersion>" + t.$XML(e).find("logicVersion").eq(0).text() + "</encoderVersion>"), r.push("<encoderReleasedDate>" + t.$XML(e).find("logicReleasedDate").eq(0).text() + "</encoderReleasedDate>"), r.push("</DeviceInfo>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, vt.prototype.getSystemCapa = function (e, t) {
                var n = X(this.CGI.systemCapabilities, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", async: !1, url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getAnalogChannelInfo = function (e, n) {
                var r = X(this.CGI.getAnalogChannelInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        var r = [];
                        r.push("<VideoInputChannelList>");
                        for (var i = t.$XML(e).find("VideoInputChannel", !0), o = 0, s = i.length; s > o; o++) {
                            var a = i[o];
                            r.push("<VideoInputChannel>"), r.push("<id>" + t.$XML(a).find("id").eq(0).text() + "</id>"), r.push("<inputPort>" + t.$XML(a).find("inputPort").eq(0).text() + "</inputPort>"), r.push("<name>" + S.escape(t.$XML(a).find("name").eq(0).text()) + "</name>"), r.push("<videoFormat>" + t.$XML(a).find("videoFormat").eq(0).text() + "</videoFormat>"), r.push("</VideoInputChannel>")
                        }
                        r.push("</VideoInputChannelList>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, vt.prototype.getDigitalChannel = function (e, n) {
                var r = X(this.CGI.getDigitalChannel, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        var r = [];
                        r.push("<InputProxyChannelList>");
                        for (var i = t.$XML(e).find("DynVideoInputChannel", !0), o = 0, s = i.length; s > o; o++) {
                            var a = i[o];
                            r.push("<InputProxyChannel>"), r.push("<id>" + t.$XML(a).find("id").eq(0).text() + "</id>"), r.push("<name>" + S.escape(t.$XML(a).find("name").eq(0).text()) + "</name>"), r.push("</InputProxyChannel>")
                        }
                        r.push("</InputProxyChannelList>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, vt.prototype.getDigitalChannelInfo = function (e, n) {
                var r = null, i = {};
                if (this.getDigitalChannel(e, {
                    async: !1, success: function (e) {
                        r = e;
                        for (var n = t.$XML(r).find("InputProxyChannel", !0), o = 0, s = n.length; s > o; o++) {
                            var a = n[o], u = t.$XML(a).find("id").eq(0).text(),
                                l = t.$XML(a).find("name").eq(0).text();
                            i[u] = l
                        }
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), null !== r) {
                    var o = X(this.CGI.getDigitalChannelInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), s = new mt,
                        a = {type: "GET", url: o, auth: e.szAuth, success: null, error: null};
                    S.extend(a, n), S.extend(a, {
                        success: function (e) {
                            var r = [];
                            r.push("<InputProxyChannelStatusList>");
                            for (var o = t.$XML(e).find("DynVideoInputChannelStatus", !0), s = 0, a = o.length; a > s; s++) {
                                var u = o[s], l = t.$XML(u).find("id").eq(0).text();
                                r.push("<InputProxyChannelStatus>"), r.push("<id>" + l + "</id>"), r.push("<sourceInputPortDescriptor>"), r.push("<proxyProtocol>" + t.$XML(u).find("adminProtocol").eq(0).text() + "</proxyProtocol>"), r.push("<addressingFormatType>" + t.$XML(u).find("addressingFormatType").eq(0).text() + "</addressingFormatType>"), r.push("<ipAddress>" + t.$XML(u).find("ipAddress").eq(0).text() + "</ipAddress>"), r.push("<managePortNo>" + t.$XML(u).find("adminPortNo").eq(0).text() + "</managePortNo>"), r.push("<srcInputPort>" + t.$XML(u).find("srcInputPort").eq(0).text() + "</srcInputPort>"), r.push("<userName>" + S.escape(t.$XML(u).find("userName").eq(0).text()) + "</userName>"), r.push("<streamType>" + t.$XML(u).find("streamType").eq(0).text() + "</streamType>"), r.push("<online>" + t.$XML(u).find("online").eq(0).text() + "</online>"), r.push("<name>" + S.escape(i[l]) + "</name>"), r.push("</sourceInputPortDescriptor>"), r.push("</InputProxyChannelStatus>")
                            }
                            r.push("</InputProxyChannelStatusList>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                        }, error: function (e, t) {
                            n.error && n.error(e, t)
                        }
                    }), s.setRequestParam(a), s.submitRequest()
                }
            }, vt.prototype.getZeroChannelInfo = function (e, t) {
                var n = X(this.CGI.getZeroChannelInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getPPPoEStatus = function (e, t) {
                var n = X(this.CGI.getPPPoEStatus, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getUPnPPortStatus = function (e, t) {
                var n = X(this.CGI.getUPnPPortStatus, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getNetworkBond = function (e, t) {
                var n = X(this.CGI.getNetworkBond, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getNetworkInterface = function (e, t) {
                var n = X(this.CGI.getNetworkInterface, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getPortInfo = function (e, n) {
                var r = X(this.CGI.getPortInfo, e.szHttpProtocol, e.szIP, e.iCGIPort), i = new mt,
                    o = {type: "GET", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (r) {
                        var i = [];
                        i.push("<AdminAccessProtocolList>");
                        for (var o = t.$XML(r).find("AdminAccessProtocol", !0), s = 0, a = o.length; a > s; s++) o[s], i.push("<AdminAccessProtocol>"), i.push("<id>" + t.$XML(r).find("id").eq(0).text() + "</id>"), i.push("<enabled>" + t.$XML(r).find("enabled").eq(0).text() + "</enabled>"), i.push("<protocol>" + t.$XML(r).find("protocol").eq(0).text().toUpperCase() + "</protocol>"), i.push("<portNo>" + t.$XML(r).find("portNo").eq(0).text() + "</portNo>"), i.push("</AdminAccessProtocol>");
                        P.getStreamChannels(e, {
                            async: !1, success: function (r) {
                                if (t.$XML(r).find("rtspPortNo", !0).length > 0) {
                                    var o = parseInt(t.$XML(r).find("rtspPortNo").eq(0).text(), 10);
                                    i.push("<AdminAccessProtocol>"), i.push("<id>4</id>"), i.push("<enabled>true</enabled>"), i.push("<protocol>RTSP</protocol>"), i.push("<portNo>" + o + "</portNo>"), i.push("</AdminAccessProtocol>"), i.push("</AdminAccessProtocolList>");
                                    var s = S.loadXML(i.join(""));
                                    n.success && n.success(s)
                                } else P.getStreamDynChannels(e, {
                                    async: !1, success: function (e) {
                                        if (t.$XML(e).find("rtspPortNo", !0).length > 0) {
                                            var r = parseInt(t.$XML(e).find("rtspPortNo").eq(0).text(), 10);
                                            i.push("<AdminAccessProtocol>"), i.push("<id>4</id>"), i.push("<enabled>true</enabled>"), i.push("<protocol>RTSP</protocol>"), i.push("<portNo>" + r + "</portNo>"), i.push("</AdminAccessProtocol>"), i.push("</AdminAccessProtocolList>");
                                            var o = S.loadXML(i.join(""));
                                            n.success && n.success(o)
                                        }
                                    }, error: function () {
                                        n.error && n.error()
                                    }
                                })
                            }, error: function () {
                                n.error && n.error()
                            }
                        })
                    }, error: function () {
                        var r = [];
                        r.push("<AdminAccessProtocolList>"), P.getStreamChannels(e, {
                            async: !1, success: function (i) {
                                if (t.$XML(i).find("rtspPortNo", !0).length > 0) {
                                    var o = parseInt(t.$XML(i).find("rtspPortNo").eq(0).text(), 10);
                                    r.push("<AdminAccessProtocol>"), r.push("<id>4</id>"), r.push("<enabled>true</enabled>"), r.push("<protocol>RTSP</protocol>"), r.push("<portNo>" + o + "</portNo>"), r.push("</AdminAccessProtocol>"), r.push("</AdminAccessProtocolList>");
                                    var s = S.loadXML(r.join(""));
                                    n.success && n.success(s)
                                } else P.getStreamDynChannels(e, {
                                    async: !1, success: function (e) {
                                        if (t.$XML(e).find("rtspPortNo", !0).length > 0) {
                                            var i = parseInt(t.$XML(e).find("rtspPortNo").eq(0).text(), 10);
                                            r.push("<AdminAccessProtocol>"), r.push("<id>4</id>"), r.push("<enabled>true</enabled>"), r.push("<protocol>RTSP</protocol>"), r.push("<portNo>" + i + "</portNo>"), r.push("</AdminAccessProtocol>"), r.push("</AdminAccessProtocolList>");
                                            var o = S.loadXML(r.join(""));
                                            n.success && n.success(o)
                                        }
                                    }, error: function () {
                                        n.error && n.error()
                                    }
                                })
                            }, error: function () {
                                n.error && n.error()
                            }
                        })
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, vt.prototype.getStreamChannels = function (e, t) {
                if (0 != e.iAnalogChannelNum) var n = X(this.CGI.getStreamChannels.analog, e.szHttpProtocol, e.szIP, e.iCGIPort); else var n = X(this.CGI.getStreamChannels.digital, e.szHttpProtocol, e.szIP, e.iCGIPort);
                var r = new mt, i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.getStreamDynChannels = function (e, t) {
                var n = X(this.CGI.getStreamDynChannels, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "GET", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.startRealPlay = function (e, t) {
                var n = 100 * t.iChannelID + t.iStreamType, r = "", i = e.szIP;
                "rtsp://" === t.urlProtocol && (i = U(i)), r = t.bZeroChannel ? X(t.cgi.zeroChannels, t.urlProtocol, i, t.iPort, n) : X(t.cgi.channels, t.urlProtocol, i, t.iPort, n);
                var o = f.HWP_Play(r, e.szAuth, t.iWndIndex, "", "");
                if (0 == o) {
                    var s = new pt;
                    s.iIndex = t.iWndIndex, s.szIP = e.szIP, s.iCGIPort = e.iCGIPort, s.szDeviceIdentify = e.szDeviceIdentify, s.iChannelID = t.iChannelID, s.iPlayStatus = _, y.push(s)
                }
                return o
            }, vt.prototype.startVoiceTalk = function (e, t) {
                var n = X(this.CGI.startVoiceTalk.open, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    r = X(this.CGI.startVoiceTalk.close, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    i = X(this.CGI.startVoiceTalk.audioData, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    o = f.HWP_StartVoiceTalkEx(n, r, i, e.szAuth, e.iAudioTypeoDeviceInfo.m_iAudioBitRate, e.m_iAudioSamplingRate);
                return o
            }, vt.prototype.ptzAutoControl = function (e, t, n, r) {
                var i = n.iChannelID, o = "", s = "";
                if (r.iPTZSpeed = r.iPTZSpeed < 7 ? 15 * r.iPTZSpeed : 100, t && (r.iPTZSpeed = 0), e.szDeviceType != H) o = X(this.CGI.ptzAutoControl, e.szHttpProtocol, e.szIP, e.iCGIPort, i), s = "<?xml version='1.0' encoding='UTF-8'?><PTZData><pan>" + r.iPTZSpeed + "</pan>" + "<tilt>0</tilt>" + "</PTZData>"; else {
                    var a = 99;
                    t && (a = 96), o = X(this.CGI.goPreset, e.szHttpProtocol, e.szIP, e.iCGIPort, i, a)
                }
                var u = new mt,
                    l = {type: "PUT", url: o, async: !1, auth: e.szAuth, data: s, success: null, error: null}, c = this;
                S.extend(l, r), S.extend(l, {
                    success: function (e) {
                        n.bPTZAuto = !n.bPTZAuto, r.success && r.success(e)
                    }, error: function (t, i) {
                        if (e.szDeviceType != H) {
                            o = X(c.CGI.ptzControl, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID);
                            var a = new mt, u = {
                                type: "PUT",
                                url: o,
                                async: !1,
                                auth: e.szAuth,
                                data: s,
                                success: null,
                                error: null
                            };
                            S.extend(u, r), a.setRequestParam(u), a.submitRequest()
                        } else r.error && r.error(t, i)
                    }
                }), u.setRequestParam(l), u.submitRequest()
            }, vt.prototype.ptzControl = function (e, t, n, r) {
                var i = (n.iChannelID, "");
                n.bPTZAuto && this.ptzAutoControl(e, !0, n, {iPTZSpeed: 0}), r.iPTZSpeed = t ? 0 : r.iPTZSpeed < 7 ? 15 * r.iPTZSpeed : 100;
                var o = [{}, {pan: 0, tilt: r.iPTZSpeed}, {pan: 0, tilt: -r.iPTZSpeed}, {
                        pan: -r.iPTZSpeed,
                        tilt: 0
                    }, {pan: r.iPTZSpeed, tilt: 0}, {pan: -r.iPTZSpeed, tilt: r.iPTZSpeed}, {
                        pan: -r.iPTZSpeed,
                        tilt: -r.iPTZSpeed
                    }, {pan: r.iPTZSpeed, tilt: r.iPTZSpeed}, {
                        pan: r.iPTZSpeed,
                        tilt: -r.iPTZSpeed
                    }, {}, {speed: r.iPTZSpeed}, {speed: -r.iPTZSpeed}, {speed: r.iPTZSpeed}, {speed: -r.iPTZSpeed}, {speed: r.iPTZSpeed}, {speed: -r.iPTZSpeed}],
                    s = "", a = {};
                switch (r.iPTZIndex) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        a = this.CGI.ptzControl, s = "<?xml version='1.0' encoding='UTF-8'?><PTZData><pan>" + o[r.iPTZIndex].pan + "</pan>" + "<tilt>" + o[r.iPTZIndex].tilt + "</tilt>" + "</PTZData>";
                        break;
                    case 10:
                    case 11:
                        a = this.CGI.ptzControl, s = "<?xml version='1.0' encoding='UTF-8'?><PTZData><zoom>" + o[r.iPTZIndex].speed + "</zoom>" + "</PTZData>";
                        break;
                    case 12:
                    case 13:
                        a = this.CGI.ptzFocus, s = "<?xml version='1.0' encoding='UTF-8'?><FocusData><focus>" + o[r.iPTZIndex].speed + "</focus>" + "</FocusData>";
                        break;
                    case 14:
                    case 15:
                        a = this.CGI.ptzIris, s = "<?xml version='1.0' encoding='UTF-8'?><IrisData><iris>" + o[r.iPTZIndex].speed + "</iris>" + "</IrisData>";
                        break;
                    default:
                        return r.error && r.error(), void 0
                }
                i = X(a, e.szHttpProtocol, e.szIP, e.iCGIPort, n.iChannelID);
                var u = new mt,
                    l = {type: "PUT", url: i, async: !1, auth: e.szAuth, data: s, success: null, error: null};
                S.extend(l, r), S.extend(l, {
                    success: function (e) {
                        r.success && r.success(e)
                    }, error: function (e, t) {
                        r.error && r.error(e, t)
                    }
                }), u.setRequestParam(l), u.submitRequest()
            }, vt.prototype.setPreset = function (e, t, n) {
                var r = (t.iChannelID, ""), i = "";
                r = X(this.CGI.setPreset, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID, n.iPresetID), i = "<?xml version='1.0' encoding='UTF-8'?>", i += "<PTZPreset>", i += "<id>" + n.iPresetID + "</id>", e.szDeviceType != H && (i += "<presetName>Preset" + n.iPresetID + "</presetName>"), i += "</PTZPreset>";
                var o = new mt, s = {type: "PUT", url: r, auth: e.szAuth, data: i, success: null, error: null};
                S.extend(s, n), S.extend(s, {
                    success: function (e) {
                        n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), o.setRequestParam(s), o.submitRequest()
            }, vt.prototype.goPreset = function (e, t, n) {
                var r = (t.iChannelID, "");
                r = X(this.CGI.goPreset, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID, n.iPresetID);
                var i = new mt, o = {type: "PUT", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, vt.prototype.recordSearch = function (e, n) {
                var r = "", i = "", o = n.iChannelID, a = n.iStreamType, u = n.szStartTime.replace(" ", "T") + "Z",
                    l = n.szEndTime.replace(" ", "T") + "Z";
                r = X(this.CGI.recordSearch, e.szHttpProtocol, e.szIP, e.iCGIPort), i = "<?xml version='1.0' encoding='UTF-8'?><CMSearchDescription><searchID>" + new s + "</searchID>" + "<trackList><trackID>" + (100 * o + a) + "</trackID></trackList>" + "<timeSpanList>" + "<timeSpan>" + "<startTime>" + u + "</startTime>" + "<endTime>" + l + "</endTime>" + "</timeSpan>" + "</timeSpanList>" + "<maxResults>40</maxResults>" + "<searchResultPostion>" + n.iSearchPos + "</searchResultPostion>" + "<metadataList>" + "<metadataDescriptor>//metadata.psia.org/VideoMotion</metadataDescriptor>" + "</metadataList>" + "</CMSearchDescription>";
                var c = new mt, d = {type: "POST", url: r, auth: e.szAuth, data: i, success: null, error: null};
                S.extend(d, n), S.extend(d, {
                    success: function (e) {
                        var r = [];
                        r.push("<CMSearchResult>"), r.push("<responseStatus>" + t.$XML(e).find("responseStatus").eq(0).text() + "</responseStatus>"), r.push("<responseStatusStrg>" + t.$XML(e).find("responseStatusStrg").eq(0).text() + "</responseStatusStrg>"), r.push("<numOfMatches>" + t.$XML(e).find("numOfMatches").eq(0).text() + "</numOfMatches>"), r.push("<matchList>");
                        for (var i = t.$XML(e).find("searchMatchItem", !0), o = 0, s = i.length; s > o; o++) {
                            var a = i[o];
                            r.push("<searchMatchItem>"), r.push("<trackID>" + t.$XML(a).find("trackID").eq(0).text() + "</trackID>"), r.push("<startTime>" + t.$XML(a).find("startTime").eq(0).text() + "</startTime>"), r.push("<endTime>" + t.$XML(a).find("endTime").eq(0).text() + "</endTime>"), r.push("<playbackURI>" + S.escape(t.$XML(a).find("playbackURI").eq(0).text()) + "</playbackURI>"), r.push("<metadataDescriptor>" + t.$XML(a).find("metadataDescriptor").eq(0).text().split("/")[1] + "</metadataDescriptor>"), r.push("</searchMatchItem>")
                        }
                        r.push("</matchList>"), r.push("</CMSearchResult>"), e = S.loadXML(r.join("")), n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), c.setRequestParam(d), c.submitRequest()
            }, vt.prototype.startPlayback = function (e, t) {
                var n = t.iWndIndex, r = t.szStartTime, i = t.szEndTime, o = e.szIP;
                "rtsp://" === t.urlProtocol && (o = U(o));
                var s = X(t.cgi, t.urlProtocol, o, t.iPort, t.iChannelID, r, i), a = f.HWP_Play(s, e.szAuth, n, r, i);
                if (0 == a) {
                    var u = new pt;
                    u.iIndex = n, u.szIP = e.szIP, u.iCGIPort = e.iCGIPort, u.szDeviceIdentify = e.szDeviceIdentify, u.iChannelID = t.iChannelID, u.iPlayStatus = D, y.push(u)
                }
                return a
            }, vt.prototype.reversePlayback = function (e, t) {
                var n = t.iWndIndex, r = t.szStartTime, i = t.szEndTime, o = e.szIP;
                "rtsp://" === t.urlProtocol && (o = U(o));
                var s = X(t.cgi, t.urlProtocol, o, t.iPort, t.iChannelID, r, i),
                    a = f.HWP_ReversePlay(s, e.szAuth, n, r, i);
                if (0 == a) {
                    var u = new pt;
                    u.iIndex = n, u.szIP = e.szIP, u.iCGIPort = e.iCGIPort, u.szDeviceIdentify = e.szDeviceIdentify, u.iChannelID = t.iChannelID, u.iPlayStatus = z, y.push(u)
                }
                return a
            }, vt.prototype.startDownloadRecord = function (e, t) {
                var n = X(this.CGI.startDownloadRecord, e.szHttpProtocol, e.szIP, e.iCGIPort),
                    r = "<?xml version='1.0' encoding='UTF-8'?><downloadRequest><playbackURI> " + S.escape(t.szPlaybackURI) + "</playbackURI>" + "</downloadRequest>";
                return f.HWP_StartDownload(n, e.szAuth, t.szFileName, r, t.bDateDir)
            }, vt.prototype.exportDeviceConfig = function (e) {
                var t = X(this.CGI.downloaddeviceConfig, e.szHttpProtocol, e.szIP, e.iCGIPort);
                return lt() ? -1 : f.HWP_ExportDeviceConfig(t, e.szAuth, "", 0)
            }, vt.prototype.importDeviceConfig = function (e, t) {
                var n = X(this.CGI.uploaddeviceConfig, e.szHttpProtocol, e.szIP, e.iCGIPort);
                return f.HWP_ImportDeviceConfig(n, e.szAuth, t.szFileName, 0)
            }, vt.prototype.restart = function (e, t) {
                var n = X(this.CGI.restart, e.szHttpProtocol, e.szIP, e.iCGIPort), r = new mt,
                    i = {type: "PUT", url: n, auth: e.szAuth, success: null, error: null};
                S.extend(i, t), S.extend(i, {
                    success: function (e) {
                        t.success && t.success(e)
                    }, error: function (e, n) {
                        t.error && t.error(e, n)
                    }
                }), r.setRequestParam(i), r.submitRequest()
            }, vt.prototype.restore = function (e, t, n) {
                var r = X(this.CGI.restore, e.szHttpProtocol, e.szIP, e.iCGIPort, t), i = new mt,
                    o = {type: "PUT", url: r, auth: e.szAuth, success: null, error: null};
                S.extend(o, n), S.extend(o, {
                    success: function (e) {
                        n.success && n.success(e)
                    }, error: function (e, t) {
                        n.error && n.error(e, t)
                    }
                }), i.setRequestParam(o), i.submitRequest()
            }, vt.prototype.startUpgrade = function (e, t) {
                var n = X(this.CGI.startUpgrade.upgrade, e.szHttpProtocol, e.szIP, e.iCGIPort),
                    r = X(this.CGI.startUpgrade.status, e.szHttpProtocol, e.szIP, e.iCGIPort);
                return f.HWP_StartUpgrade(n, r, e.szAuth, t.szFileName)
            }, vt.prototype.set3DZoom = function (e, t, n, r) {
                var i = X(this.CGI.set3DZoom, e.szHttpProtocol, e.szIP, e.iCGIPort, t.iChannelID), o = new mt,
                    s = {type: "PUT", url: i, data: n, auth: e.szAuth, success: null, error: null};
                S.extend(s, r), S.extend(s, {
                    success: function (e) {
                        r.success && r.success(e)
                    }, error: function (e, t) {
                        r.error && r.error(e, t)
                    }
                }), o.setRequestParam(s), o.submitRequest()
            }, vt.prototype.deviceCapturePic = function (e, t, n, r) {
                var t = 100 * t + 1, i = X(this.CGI.deviceCapture.channels, e.szHttpProtocol, e.szIP, e.iCGIPort, t),
                    o = [];
                return S.isInt(r.iResolutionWidth) && o.push("videoResolutionWidth=" + r.iResolutionWidth), S.isInt(r.iResolutionHeight) && o.push("videoResolutionHeight=" + r.iResolutionHeight), o.length > 0 && (i += "?" + o.join("&")), f.HWP_DeviceCapturePic(i, e.szAuth, n, r.bDateDir)
            };
            var gt = function () {
            };
            gt.prototype._alert = function (e) {
                d.bDebugMode && console.log(e)
            }, function (e) {
                var t = function (e) {
                    this.elems = [], this.length = 0, this.length = this.elems.push(e)
                };
                t.prototype.find = function (e, t) {
                    var n = this.elems[this.length - 1] ? this.elems[this.length - 1].getElementsByTagName(e) : [];
                    return this.length = this.elems.push(n), t ? n : this
                }, t.prototype.eq = function (e, t) {
                    var n = this.elems[this.length - 1].length, r = null;
                    return n > 0 && n > e && (r = this.elems[this.length - 1][e]), this.length = this.elems.push(r), t ? r : this
                }, t.prototype.text = function (e) {
                    return this.elems[this.length - 1] ? e ? (window.DOMParser ? this.elems[this.length - 1].textContent = e : this.elems[this.length - 1].text = e, void 0) : window.DOMParser ? this.elems[this.length - 1].textContent : this.elems[this.length - 1].text : ""
                }, t.prototype.attr = function (e) {
                    if (this.elems[this.length - 1]) {
                        var t = this.elems[this.length - 1].attributes.getNamedItem(e);
                        return t ? t.value : ""
                    }
                }, e.$XML = function (e) {
                    return new t(e)
                }
            }(this);
            var Pt = function () {
            };
            Pt.prototype.extend = function () {
                for (var e, t = arguments[0] || {}, n = 1, r = arguments.length; r > n; n++) if (null != (e = arguments[n])) for (var i in e) {
                    var o = (t[i], e[i]);
                    t !== o && ("object" == typeof o ? t[i] = this.extend({}, o) : void 0 !== o && (t[i] = o))
                }
                return t
            }, Pt.prototype.browser = function () {
                var e = /(chrome)[ \/]([\w.]+)/, t = /(safari)[ \/]([\w.]+)/, n = /(opera)(?:.*version)?[ \/]([\w.]+)/,
                    r = /(msie) ([\w.]+)/, i = /(trident.*rv:)([\w.]+)/, o = /(mozilla)(?:.*? rv:([\w.]+))?/,
                    s = navigator.userAgent.toLowerCase(),
                    a = e.exec(s) || t.exec(s) || n.exec(s) || r.exec(s) || i.exec(s) || s.indexOf("compatible") < 0 && o.exec(s) || ["unknow", "0"];
                a.length > 0 && a[1].indexOf("trident") > -1 && (a[1] = "msie");
                var u = {};
                return u[a[1]] = !0, u.version = a[2], u
            }, Pt.prototype.loadXML = function (e) {
                if (null == e || "" == e) return null;
                var t = null;
                if (window.DOMParser) {
                    var n = new DOMParser;
                    t = n.parseFromString(e, "text/xml")
                } else t = new ActiveXObject("Microsoft.XMLDOM"), t.async = !1, t.loadXML(e);
                return t
            }, Pt.prototype.toXMLStr = function (e) {
                var t = "";
                try {
                    var n = new XMLSerializer;
                    t = n.serializeToString(e)
                } catch (r) {
                    try {
                        t = e.xml
                    } catch (r) {
                        return ""
                    }
                }
                return -1 == t.indexOf("<?xml") && (t = "<?xml version='1.0' encoding='utf-8'?>" + t), t
            }, Pt.prototype.escape = function (e) {
                return e ? e.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;") : e
            }, Pt.prototype.encodePwd = function (e, t, n) {
                var r = "";
                if (n) {
                    r = S.sha256(t.userName + t.salt + e), r = S.sha256(r + t.challenge);
                    for (var i = 2; i < t.iIterate; i++) r = S.sha256(r)
                } else {
                    r = S.sha256(e) + t.challenge;
                    for (var i = 1; i < t.iIterate; i++) r = S.sha256(r)
                }
                return r
            }, Pt.prototype.dateFormat = function (e, t) {
                var n = {
                    "M+": e.getMonth() + 1,
                    "d+": e.getDate(),
                    "h+": e.getHours(),
                    "m+": e.getMinutes(),
                    "s+": e.getSeconds(),
                    "q+": Math.floor((e.getMonth() + 3) / 3),
                    S: e.getMilliseconds()
                };
                /(y+)/.test(t) && (t = t.replace(RegExp.$1, (e.getFullYear() + "").substr(4 - RegExp.$1.length)));
                for (var r in n) new RegExp("(" + r + ")").test(t) && (t = t.replace(RegExp.$1, 1 == RegExp.$1.length ? n[r] : ("00" + n[r]).substr(("" + n[r]).length)));
                return t
            }, Pt.prototype.Base64 = {
                _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=", encode: function (e) {
                    var t, n, r, i, o, s, a, u = "", l = 0;
                    for (e = Pt.prototype.Base64._utf8_encode(e); l < e.length;) t = e.charCodeAt(l++), n = e.charCodeAt(l++), r = e.charCodeAt(l++), i = t >> 2, o = (3 & t) << 4 | n >> 4, s = (15 & n) << 2 | r >> 6, a = 63 & r, isNaN(n) ? s = a = 64 : isNaN(r) && (a = 64), u = u + this._keyStr.charAt(i) + this._keyStr.charAt(o) + this._keyStr.charAt(s) + this._keyStr.charAt(a);
                    return u
                }, decode: function (e) {
                    var t, n, r, i, o, s, a, u = "", l = 0;
                    for (e = e.replace(/[^A-Za-z0-9\+\/\=]/g, ""); l < e.length;) i = this._keyStr.indexOf(e.charAt(l++)), o = this._keyStr.indexOf(e.charAt(l++)), s = this._keyStr.indexOf(e.charAt(l++)), a = this._keyStr.indexOf(e.charAt(l++)), t = i << 2 | o >> 4, n = (15 & o) << 4 | s >> 2, r = (3 & s) << 6 | a, u += String.fromCharCode(t), 64 != s && (u += String.fromCharCode(n)), 64 != a && (u += String.fromCharCode(r));
                    return u = Pt.prototype.Base64._utf8_decode(u)
                }, _utf8_encode: function (e) {
                    e = e.replace(/\r\n/g, "\n");
                    for (var t = "", n = 0; n < e.length; n++) {
                        var r = e.charCodeAt(n);
                        128 > r ? t += String.fromCharCode(r) : r > 127 && 2048 > r ? (t += String.fromCharCode(192 | r >> 6), t += String.fromCharCode(128 | 63 & r)) : (t += String.fromCharCode(224 | r >> 12), t += String.fromCharCode(128 | 63 & r >> 6), t += String.fromCharCode(128 | 63 & r))
                    }
                    return t
                }, _utf8_decode: function (e) {
                    for (var t = "", n = 0, r = c1 = c2 = 0; n < e.length;) r = e.charCodeAt(n), 128 > r ? (t += String.fromCharCode(r), n++) : r > 191 && 224 > r ? (c2 = e.charCodeAt(n + 1), t += String.fromCharCode((31 & r) << 6 | 63 & c2), n += 2) : (c2 = e.charCodeAt(n + 1), c3 = e.charCodeAt(n + 2), t += String.fromCharCode((15 & r) << 12 | (63 & c2) << 6 | 63 & c3), n += 3);
                    return t
                }
            }, Pt.prototype.createEventScript = function (e, t, n) {
                var r = document.createElement("script");
                r.htmlFor = e, r.event = t, r.innerHTML = n, document.body.parentNode.appendChild(r)
            }, Pt.prototype.isInt = function (e) {
                return /^\d+$/.test(e)
            }, Pt.prototype.getDirName = function () {
                var e = "";
                if ("" !== d.szBasePath) e = d.szBasePath; else {
                    var t = /[^?#]*\//, n = document.getElementById("videonode");
                    if (n) e = n.src.match(t)[0]; else {
                        for (var r = document.scripts, i = 0, o = r.length; o > i; i++) if (r[i].src.indexOf("webVideoCtrl.js") > -1) {
                            n = r[i];
                            break
                        }
                        n && (e = n.src.match(t)[0])
                    }
                }
                return e
            }, Pt.prototype.loadScript = function (e, t) {
                var n = document.createElement("script");
                n.type = "text/javascript", n.onload = function () {
                    t()
                }, n.src = e, document.getElementsByTagName("head")[0].appendChild(n)
            }, Pt.prototype.encodeString = function (e) {
                return e ? e.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;") : ""
            }, Pt.prototype.getIrreversibleKey = function (e, t) {
                var n = e;
                if (oSecurityCap.oIrreversibleEncrypt.bSupport) {
                    var r = oSecurityCap.oIrreversibleEncrypt.salt;
                    return S.sha256(t + r + e)
                }
                return n
            }, Pt.prototype.strToAESKey = function (e, t) {
                var n = "";
                if (oSecurityCap.iKeyIterateNum > 0) {
                    n = S.sha256(S.getIrreversibleKey(e, t) + "AaBbCcDd1234!@#$");
                    for (var r = 1; r < oSecurityCap.iKeyIterateNum; r++) n = S.sha256(n)
                }
                return n = n && n.substring(0, 32)
            }, Pt.prototype.exportPasswordDeviceConfig = function (e, t) {
                var n = MD5((new Date).getTime().toString()),
                    r = S.encodeAES(S.Base64.encode(S.encodeString(t)), szAESKey, n);
                return e + "?secretkey=" + r + "&security=1&iv=" + n
            }, Pt.prototype.encodeAES = function (e, t, n, r) {
                var i = "";
                if ("ecb" === r) for (var o = e.length, s = 0; o > 0;) i += o > 16 ? aes_encrypt(e.substring(s, s + 16), t, !0) : aes_encrypt(e.substring(s), t, !0), o -= 16, s += 16; else {
                    "undefined" == typeof n && (n = "6cd9616beb39d4034fdebe107df9a399");
                    var a = CryptoJS.enc.Hex.parse(t), u = CryptoJS.enc.Hex.parse(n),
                        l = CryptoJS.AES.encrypt(e, a, {mode: CryptoJS.mode.CBC, iv: u, padding: CryptoJS.pad.Pkcs7});
                    i = l.ciphertext.toString()
                }
                return i
            }, Pt.prototype.sha256 = function (e) {
                function t(e, t) {
                    var n = (65535 & e) + (65535 & t);
                    return (e >> 16) + (t >> 16) + (n >> 16) << 16 | 65535 & n
                }

                function n(e, t) {
                    return e >>> t | e << 32 - t
                }

                return e = function (e) {
                    for (var e = e.replace(/\r\n/g, "\n"), t = "", n = 0; n < e.length; n++) {
                        var r = e.charCodeAt(n);
                        128 > r ? t += String.fromCharCode(r) : (r > 127 && 2048 > r ? t += String.fromCharCode(192 | r >> 6) : (t += String.fromCharCode(224 | r >> 12), t += String.fromCharCode(128 | 63 & r >> 6)), t += String.fromCharCode(128 | 63 & r))
                    }
                    return t
                }(e), function (e) {
                    for (var t = "", n = 0; n < 4 * e.length; n++) t += "0123456789abcdef".charAt(15 & e[n >> 2] >> 8 * (3 - n % 4) + 4) + "0123456789abcdef".charAt(15 & e[n >> 2] >> 8 * (3 - n % 4));
                    return t
                }(function (e, r) {
                    var i, o, s, a, u, l, c, d, f, h, p, m,
                        y = [1116352408, 1899447441, 3049323471, 3921009573, 961987163, 1508970993, 2453635748, 2870763221, 3624381080, 310598401, 607225278, 1426881987, 1925078388, 2162078206, 2614888103, 3248222580, 3835390401, 4022224774, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, 2554220882, 2821834349, 2952996808, 3210313671, 3336571891, 3584528711, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, 2177026350, 2456956037, 2730485921, 2820302411, 3259730800, 3345764771, 3516065817, 3600352804, 4094571909, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, 2227730452, 2361852424, 2428436474, 2756734187, 3204031479, 3329325298],
                        v = [1779033703, 3144134277, 1013904242, 2773480762, 1359893119, 2600822924, 528734635, 1541459225],
                        g = Array(64);
                    for (e[r >> 5] |= 128 << 24 - r % 32, e[(r + 64 >> 9 << 4) + 15] = r, f = 0; f < e.length; f += 16) {
                        for (i = v[0], o = v[1], s = v[2], a = v[3], u = v[4], l = v[5], c = v[6], d = v[7], h = 0; 64 > h; h++) g[h] = 16 > h ? e[h + f] : t(t(t(n(g[h - 2], 17) ^ n(g[h - 2], 19) ^ g[h - 2] >>> 10, g[h - 7]), n(g[h - 15], 7) ^ n(g[h - 15], 18) ^ g[h - 15] >>> 3), g[h - 16]), p = t(t(t(t(d, n(u, 6) ^ n(u, 11) ^ n(u, 25)), u & l ^ ~u & c), y[h]), g[h]), m = t(n(i, 2) ^ n(i, 13) ^ n(i, 22), i & o ^ i & s ^ o & s), d = c, c = l, l = u, u = t(a, p), a = s, s = o, o = i, i = t(p, m);
                        v[0] = t(i, v[0]), v[1] = t(o, v[1]), v[2] = t(s, v[2]), v[3] = t(a, v[3]), v[4] = t(u, v[4]), v[5] = t(l, v[5]), v[6] = t(c, v[6]), v[7] = t(d, v[7])
                    }
                    return v
                }(function (e) {
                    for (var t = [], n = 0; n < 8 * e.length; n += 8) t[n >> 5] |= (255 & e.charCodeAt(n / 8)) << 24 - n % 32;
                    return t
                }(e), 8 * e.length))
            }, Pt.prototype.cookie = function (e, t, n) {
                if (arguments.length > 1 && (null === t || "object" != typeof t)) {
                    if (n = this.extend({}, n), null === t && (n.expires = -1), "number" == typeof n.expires) {
                        var r = n.expires, i = n.expires = new Date;
                        i.setDate(i.getDate() + r)
                    }
                    return document.cookie = [encodeURIComponent(e), "=", n.raw ? String(t) : encodeURIComponent(String(t)), n.expires ? "; expires=" + n.expires.toUTCString() : "", n.path ? "; path=" + n.path : "; path=/", n.domain ? "; domain=" + n.domain : "", n.secure ? "; secure" : ""].join("")
                }
                n = t || {};
                var o, s = n.raw ? function (e) {
                    return e
                } : decodeURIComponent;
                return (o = new RegExp("(?:^|; )" + encodeURIComponent(e) + "=([^;]*)").exec(document.cookie)) ? s(o[1]) : null
            }, s.prototype.valueOf = function () {
                return this.id
            }, s.prototype.toString = function () {
                return this.id
            }, s.prototype.createUUID = function () {
                var e = new Date(1582, 10, 15, 0, 0, 0, 0), t = new Date, n = t.getTime() - e.getTime(), r = "-",
                    i = s.getIntegerBits(n, 0, 31), o = s.getIntegerBits(n, 32, 47),
                    a = s.getIntegerBits(n, 48, 59) + "1", u = s.getIntegerBits(s.rand(4095), 0, 7),
                    l = s.getIntegerBits(s.rand(4095), 0, 7),
                    c = s.getIntegerBits(s.rand(8191), 0, 7) + s.getIntegerBits(s.rand(8191), 8, 15) + s.getIntegerBits(s.rand(8191), 0, 7) + s.getIntegerBits(s.rand(8191), 8, 15) + s.getIntegerBits(s.rand(8191), 0, 15);
                return i + r + o + r + a + r + u + l + r + c
            }, s.getIntegerBits = function (e, t, n) {
                var r = s.returnBase(e, 16), i = new Array, o = "", a = 0;
                for (a = 0; a < r.length; a++) i.push(r.substring(a, a + 1));
                for (a = Math.floor(t / 4); a <= Math.floor(n / 4); a++) o += i[a] && "" != i[a] ? i[a] : "0";
                return o
            }, s.returnBase = function (e, t) {
                var n = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
                if (t > e) var r = n[e]; else {
                    var i = "" + Math.floor(e / t), o = e - i * t;
                    if (i >= t) var r = this.returnBase(i, t) + n[o]; else var r = n[i] + n[o]
                }
                return r
            }, s.rand = function (e) {
                return Math.floor(Math.random() * e)
            }, g = new yt, P = new vt, v = new gt, S = new Pt;
            var St = S.dateFormat(new Date, "yyyyMMddhhmmss");
            return l = "webVideoCtrl" + St, c = "webVideoCtrl" + St, "object" != typeof window.attachEvent && S.browser().msie && (S.createEventScript(l, "GetSelectWndInfo(SelectWndInfo)", "GetSelectWndInfo(SelectWndInfo);"), S.createEventScript(l, "ZoomInfoCallback(szZoomInfo)", "ZoomInfoCallback(szZoomInfo);"), S.createEventScript(l, "GetHttpInfo(lID, lpInfo, lReverse)", "GetHttpInfo(lID, lpInfo, lReverse);"), S.createEventScript(l, "PluginEventHandler(iEventType, iParam1, iParam2)", "PluginEventHandler(iEventType, iParam1, iParam2);"), S.createEventScript(l, "RemoteConfigInfo(lID)", "RemoteConfigInfo(lID);"), S.createEventScript(l, "KeyBoardEventInfo(iKeyCode)", "KeyBoardEventInfo(iKeyCode);")), this
        }(), t = window.WebVideoCtrl = e;
        t.version = "1.1.0"
    }
}(this), "object" == typeof exports && "undefined" != typeof module || ("function" == typeof define && define.amd ? define(function () {
    return WebVideoCtrl
}) : "function" == typeof define && define.cmd && define(function (e, t, n) {
    n.exports = WebVideoCtrl
}));