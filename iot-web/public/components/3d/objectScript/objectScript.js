import {loadModalData} from 'api/modal/modalDataApi.js';
(function (vc) {
    vc.extends({
        data: {
            objectScriptInfo: {
                sceneId: '',
                scriptId: '',
                objName: '',
                objId: '',
                objType: '',
                communityId:'',
                objTypes: [],
                dtScripts:[],
                modalData:[],
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('objectScript', 'openModal', function (_data) {
                $that.objectScriptInfo.communityId = _data.communityId;
                $that.objectScriptInfo.sceneId = _data.sceneId;
                $that.objectScriptInfo.objName = _data.objName;
                vc.getDict('dt_modal', 'modal_type', function (_data) {
                    $that.objectScriptInfo.objTypes = _data;
                })
                $('#objectScriptModel').modal('show');
            });
        },
        methods: {
            objectScriptValidate() {
                return vc.validate.validate({
                    objectScriptInfo: $that.objectScriptInfo
                }, {
                    'objectScriptInfo.sceneId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "场景不能为空"
                        },
                    ],
                    'objectScriptInfo.scriptId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "脚本不能为空"
                        },
                    ],
                    'objectScriptInfo.objName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "对象不能为空"
                        }
                    ],
                });
            },
            saveDtScriptInfo: function () {
                if (!$that.objectScriptValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/dtScript.saveDtSceneObjScript',
                    JSON.stringify($that.objectScriptInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#objectScriptModel').modal('hide');
                            $that.clearAddDtScriptInfo();
                            vc.emit('dtScript', 'listDtScript', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);

                    });
            },
            clearAddDtScriptInfo: function () {
                $that.objectScriptInfo = {
                    sceneId: '',
                    scriptId: '',
                    objName: '',
                    objId: '',
                    objType: '',
                    objTypes: [],
                    dtScripts:[],
                    modalData:[],
                };
            },
            _chanageType:function(){
                let param = {
                    params: {
                        page:1,
                        row:100,
                        scriptType:$that.objectScriptInfo.objType
                    }
                };

                //发送get请求
                vc.http.apiGet('/dtScript.listDtScript',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.objectScriptInfo.dtScripts = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );

                $that.objectScriptInfo.modalData =  [];
                
                loadModalData({
                    modalType:$that.objectScriptInfo.objType,
                    communityId: $that.objectScriptInfo.communityId
                },$that.objectScriptInfo.modalData);
            }
        }
    });

})(window.vc);
