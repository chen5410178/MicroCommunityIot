/**
 编辑员工
 **/
(function(vc) {
    var _fileUrl = '/callComponent/download/getFile/fileByObjId';
    vc.extends({
        data: {
            editStaffInfo: {
                userId: '',
                username: '',
                email: '',
                tel: '',
                sex: '',
                address: '',
                errorInfo: '',
                videoPlaying: false,
                photo: '',
                photoUrl: '',
                relCd: '',
                relCds: [],
                branchOrgs: [],
                departmentOrgs: [],
                parentOrgId: '',
                parentOrgName: '',
                parentTwoOrgId: '',
                orgNewName: '',
                orgId: ''
            }
        },
        _initMethod: function() {
            vc.getDict('u_org_staff_rel', "rel_cd", function(_data) {
                $that.editStaffInfo.relCds = _data;
            });
        },
        _initEvent: function() {
            $that.$on('edit_staff_event', function(_staffInfo) {
                $that.refreshEditStaffInfo(_staffInfo);
                $that.editStaffInfo.photo = _staffInfo.faceUrl;
                $that.editStaffInfo.photoUrl = _staffInfo.faceUrl;
                $that._editGetOrgsByOrgLevelStaff(1, 100, 2, _staffInfo.parentOrgId);
                $('#editStaffModel').modal('show');
            });
        },
        methods: {
            refreshEditStaffInfo(_staffInfo) {
                vc.copyObject(_staffInfo, $that.editStaffInfo);
                $that.editStaffInfo.username = _staffInfo.name;
            },
            editStaffValidate() {
                return vc.validate.validate({
                    editStaffInfo: $that.editStaffInfo
                }, {
                    'editStaffInfo.username': [{
                            limit: "required",
                            param: "",
                            errInfo: "用户名不能为空"
                        },
                        {
                            limit: "maxin",
                            param: "2,10",
                            errInfo: "用户名长度必须在2位至10位"
                        },
                    ],
                    'editStaffInfo.tel': [{
                        limit: "required",
                        param: "",
                        errInfo: "手机号不能为空"
                    }],
                    'editStaffInfo.sex': [{
                        limit: "required",
                        param: "",
                        errInfo: "性别不能为空"
                    }],
                    'editStaffInfo.address': [{
                            limit: "required",
                            param: "",
                            errInfo: "地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "地址长度不能超过200位"
                        },
                    ]
                });
            },
            editStaffSubmit: function() {
                if (!$that.editStaffValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.editStaffInfo.name = $that.editStaffInfo.username;
                $that.editStaffInfo.staffId = $that.editStaffInfo.userId;
                $that.editStaffInfo.photoUrl = "";
                vc.http.apiPost(
                    '/user.staff.modify',
                    JSON.stringify($that.editStaffInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editStaffModel').modal('hide');
                            vc.emit('staff', 'notify', {})
                            vc.toast("修改成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                        $that.editStaffInfo.photoUrl = $that.editStaffInfo.photo;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        // $that.editStaffInfo.errorInfo = errInfo;
                        $that.editStaffInfo.photoUrl = $that.editStaffInfo.photo;
                        vc.toast(errInfo)
                    });
            },
            _uploadEditPhoto: function(event) {
                $("#uploadEditStaffPhoto").trigger("click")
            },
            _chooseEditPhoto: function(event) {
                var photoFiles = event.target.files;
                if (photoFiles && photoFiles.length > 0) {
                    // 获取目前上传的文件
                    let file = photoFiles[0]; // 文件大小校验的动作
                    if (file.size > 1024 * 1024 * 2) {
                        vc.toast("图片大小不能超过 2MB!")
                        return false;
                    }
                    // 改为异步上传图片
                    this._doUploadImageEditStaff(file);
                }
            },
            _doUploadImageEditStaff: function(_file) {
                var param = new FormData();
                param.append("uploadFile", _file);
                param.append('communityId', vc.getCurrentCommunity().communityId);
 
               vc.http.upload('/upload/uploadFile',
               param, {
                        emulateJSON: true,
                        //添加请求头
                        headers: {
                            "Content-Type": "multipart/form-data"
                        }
                    },
                    function(json, res) {
                        if (res.status != 200) {
                            vc.toast("上传文件失败");
                            return;
                        }
                        var _json = JSON.parse(json);
                        $that.editStaffInfo.photo = _json.data.fileId;
                        $that.editStaffInfo.photoUrl = _json.data.url;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    }
                );
            },
            _editGetOrgsByOrgLevelStaff: function(_page, _rows, _orgLevel, _parentOrgId) {
                let param = {
                    params: {
                        page: _page,
                        row: _rows,
                        orgLevel: _orgLevel,
                        parentOrgId: _parentOrgId
                    }
                };
                //发送get请求
                vc.http.apiGet('/org.listOrgs',
                    param,
                    function(json, res) {
                        var _orgInfo = JSON.parse(json);
                        if (_orgLevel == 2) {
                            $that.editStaffInfo.branchOrgs = _orgInfo.data;
                            $that._editGetOrgsByOrgLevelStaff(1, 100, 3, $that.editStaffInfo.parentTwoOrgId);
                        } else {
                            $that.editStaffInfo.departmentOrgs = _orgInfo.orgs;
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _editChangeBrach: function() {
                $that._editGetOrgsByOrgLevelStaff(1, 100, 3, $that.editStaffInfo.parentTwoOrgId);
            }
        },
    });
})(window.vc);