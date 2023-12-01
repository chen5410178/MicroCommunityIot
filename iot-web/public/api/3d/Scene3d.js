import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { DragControls } from 'three/addons/controls/DragControls.js';
import Stats from 'three/addons/libs/stats.module.js';
import { OBJLoader } from 'three/addons/loaders/OBJLoader.js';
import { TransformControls } from 'three/addons/controls/TransformControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import { DRACOLoader } from 'three/addons/loaders/DRACOLoader.js'
import * as BufferGeometryUtils from 'three/addons/utils/BufferGeometryUtils.js';
import { RGBELoader } from 'three/addons/loaders/RGBELoader.js'


export default class Scene3d {

    selectObject;

    constructor(_element) {
        this._initScene(_element);
    }

    /**
     * 初始化场景
     */
    initScene() {
        this.scene = new THREE.Scene();
        //this.scene.fog = new THREE.Fog(0x111111, 150, 200);
        // show axes in the screen
        let axes = new THREE.AxesHelper(5);
        this.scene.add(axes);

        let helper = new THREE.GridHelper(50, 20, 0xCD3700, 0x4A4A4A);//网格线
        this.scene.add(helper);
    }
    /**
     * 初始化相机
     */
    initCamera() {
        this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);
        // position and point the camera to the center of the scene
        this.camera.position.set(-30, 40, 30);
        this.camera.lookAt(this.scene.position);
    }

    /**
     * 初始化渲染器
     */
    initRenderer(_element) {

        // create a render and set the size
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setClearColor(new THREE.Color(0x87CEEB));
        this.renderer.setSize(window.innerWidth - 390, window.innerHeight);
        this.renderer.antialias=true;

        this.renderer.alpha=true;
    
        this.renderer.precision='mediump'
        // add the output of the renderer to the html element
        this.container = document.getElementById(_element);
        this.container.appendChild(this.renderer.domElement);
    }

    /**
     * 性能检测
     */
    initStats() {
        this.stats = new Stats();
        this.stats.domElement.style.position = 'absolute';
        this.stats.domElement.style.left = '200px';
        this.container.appendChild(this.stats.dom);
    }

    initControls() {
        this.controls = new OrbitControls(this.camera, this.renderer.domElement);
        // 设置控制器阻尼，让控制器更有真是效果，必须在动画循环里调用update（）
        //this.controls.enableDamping = true;
    }

    /**
     * 初始化灯光
     */
    initLight() {
        this.light = new THREE.SpotLight(0xffffff);
        this.light.position.set(-300, 600, -400);
        this.light.castShadow = true;
        this.scene.add(this.light);
        this.scene.add(new THREE.AmbientLight(0x404040));
    }

    update() {
        this.controls.update();
        this.stats.update();

    }


    changeMaterial(object) {
        let material = new THREE.MeshLambertMaterial({
            color: 0xffffff * Math.random(),
            transparent: object.material.transparent ? false : true,
            opacity: 0.8
        });
        object.material = material;
    }

    onMouseDblclick(event, _that) {
        //alert("a");
        //获取raycaster和所有模型相交的数组，其中的元素按照距离排序，越近的越靠前
        let intersects = _that.getIntersects(event, _that);
        console.log(intersects);
        // console.log(intersects[0].object);

        //获取选中最近的Mesh对象
        //instance坐标是对象，右边是类，判断对象是不是属于这个类的
        let _name = ''; //&& intersects[0].object.type === 'Mesh'
        if (intersects.length !== 0) {
            //intersects[0].object.material.color.set(0x00FF00);
            //console.log(intersects[0].object.material.color);
            for (let _index = 0; _index < intersects.length; _index++) {
                _name = intersects[_index].object.name;
                if (_name == 'X' || _name == 'Y' || _name == 'Z'
                    || _name == 'XY' || _name == 'XZ' || _name == 'YZ'
                    || _name == 'XYZ' || _name == 'E' || _name == 'XYZE'
                    || _name == 'START' || _name == 'END') {
                    continue
                }
                if (intersects[_index].object.type === 'Mesh') {
                    _that.selectObject = intersects[_index].object;
                    if (_that.selectCallBack) {
                        _that.selectCallBack(_that.selectObject);
                    }
                    break;
                }
            }

            //changeMaterial(selectObject)

        } else {
            console.log('未选中 Mesh!');
        }
    }

    //获取与射线相交的对象数组
    getIntersects(event, _that) {
        event.preventDefault();// 阻止默认的点击事件执行, https://developer.mozilla.org/zh-CN/docs/Web/API/Event/preventDefault
        //console.log("event.clientX:" + event.clientX);
        //console.log("event.clientY:" + event.clientY);

        //声明 rayCaster 和 mouse 变量
        let rayCaster = new THREE.Raycaster();
        let mouse = new THREE.Vector2();

        //通过鼠标点击位置，计算出raycaster所需点的位置，以屏幕为中心点，范围-1到1
        mouse.x = ((event.clientX - _that.container.getBoundingClientRect().left) / _that.container.offsetWidth) * 2 - 1;
        mouse.y = -((event.clientY - _that.container.getBoundingClientRect().top) / _that.container.offsetHeight) * 2 + 1; //这里为什么是-号，没有就无法点中

        //通过鼠标点击的位置(二维坐标)和当前相机的矩阵计算出射线位置
        rayCaster.setFromCamera(mouse, _that.camera);

        //获取与射线相交的对象数组， 其中的元素按照距离排序，越近的越靠前。
        //+true，是对其后代进行查找，这个在这里必须加，因为模型是由很多部分组成的，后代非常多。
        let intersects = rayCaster.intersectObjects(_that.scene.children, true);

        //返回选中的对象
        return intersects;
    }

    // 窗口变动触发的方法
    onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
    }

    //键盘按下触发的方法
    onKeyDown(event, _that) {
        switch (event.keyCode) {
            case 13:
                this.initCamera();
                this.initControls();
                break;
            case 87: // W
                _that.transformControls.setMode('translate');
                break;
            case 69: // E
                _that.transformControls.setMode('rotate');
                break;
            case 82: // R
                _that.transformControls.setMode('scale');
                break;
        }
    }




    _initScene(_element) {

        this.initScene();
        this.initCamera();
        this.initRenderer(_element);
        this.initStats();
        this.initLight();
        this.initControls();

        this.transformControls = new TransformControls(this.camera, this.renderer.domElement);
        this.scene.add(this.transformControls);


        let _that = this;

        this.transformControls.addEventListener('dragging-changed', function (event) {
            _that.controls.enabled = !event.value;
        });

        addEventListener('click', function (event) {
            _that.onMouseDblclick(event, _that)
        }, false);
        //addEventListener('resize', onWindowResize, false);
        addEventListener('keydown', function (event) {
            _that.onKeyDown(event, _that);
        }, false);
        function animate() {
            // if (selectObject != undefined && selectObject != null) {
            //     //renderDiv(selectObject);
            // }
            requestAnimationFrame(animate);
            _that.renderer.render(_that.scene, _that.camera);
            _that.update(_that);
            //判断渲染器是否调整，若调整，相机也需要调整aspect
            // if (resizeRendererToDisplaySize(renderer)) {
            //     const canvas = renderer.domElement;
            //     //重新设置摄像机看视锥体的横纵比，横纵比一般为屏幕宽高比，不然会挤压变形
            //     camera.aspect = canvas.clientWidth / canvas.clientHeight;
            //     camera.updateProjectionMatrix();
            // }
        }
        animate();
    }

    setSelect3DObject(_callback) {
        this.selectCallBack = _callback;
    }

    setTransformControlModal(_modal) {
        if (!_modal) {
            _modal = 'translate';
        }
        this.transformControls.setMode(_modal);
    }

    controlObject(_object) {
        this.transformControls.attach(_object)
    }


    /**
     * 加载，模型
     * @param {模型信息} _modal 
     */
    add3DObject(_modal) {
        const loader = new OBJLoader();
        let _that = this;
        loader.load(
            // resource URL
            _modal.path,
            // called when resource is loaded
            function (object) {
                // let material = new THREE.MeshBasicMaterial({});
                // //obj模型是有无数个mesh子元素组成，所以我们可以对单个child的mesh进行操作
                // object.children.forEach(function (child) {
                //     child.material = material;
                //     // child.material=new THREE.MeshBasicMaterial({map:new THREE.TextureLoader().load('./statics/imgs/floor.jpg'),side:THREE.DoubleSide});
                //     child.geometry.computeFaceNormals();
                //     child.geometry.computeVertexNormals();
                // });
                object.scale.set(1, 1, 1);
                _that.scene.add(object);
            },
            // called when loading is in progresses
            function (xhr) {
                console.log((xhr.loaded / xhr.total * 100) + '% loaded');
            },
            // called when loading has errors
            function (error) {
                console.log('An error happened', error);
            }
        );
    }

    addGltfObject(_modal) {
        const loader = new GLTFLoader();
        const dracoLoader = new DRACOLoader();
        dracoLoader.setDecoderPath('/js/three/examples/jsm/libs/draco/');
        loader.setDRACOLoader(dracoLoader);
        let _that = this;
        loader.load(
            // resource URL
            _modal.path,
            // called when the resource is loaded
            function (gltf) {

                //需要添加的部分
                gltf.scene.traverse(function (child) {
                    if (child.isMesh) {
                        child.material.emissive = child.material.color;
                        child.material.emissiveMap = child.material.map;
                    }
                });
                _that.scene.add(gltf.scene);
                console.log(gltf.scene)
                //_that.scene.add(_that.merge(gltf.scene));

                // gltf.animations; // Array<THREE.AnimationClip>
                // gltf.scene; // THREE.Group
                // gltf.scenes; // Array<THREE.Group>
                // gltf.cameras; // Array<THREE.Camera>
                // gltf.asset; // Object

            },
            // called while loading is progressing
            function (xhr) {
                console.log((xhr.loaded / xhr.total * 100) + '% loaded');
            },
            // called when loading has errors
            function (error) {
                console.log('An error happened', error);
            }
        );
    }
    addJpgBackgroup(_modal) {
        const texture = new THREE.TextureLoader().load( _modal.path );
        texture.mapping = THREE.EquirectangularReflectionMapping;
        texture.magFilter = THREE.LinearFilter;//③
        texture.minFilter = THREE.LinearMipmapLinearFilter;//④
        texture.encoding = THREE.sRGBEncoding;
        //texture.repeat.set( 4, 4 );
        this.scene.background = texture;
        this.scene.environment = texture;
    }
    addHdrBackgroup(_modal) {
        const rgbeLoader = new RGBELoader();
        //资源较大，使用异步加载
        let _that = this;
        rgbeLoader.loadAsync(_modal.path).then((texture) => {
            texture.mapping = THREE.EquirectangularReflectionMapping;
            //将加载的材质texture设置给背景和环境
            _that.scene.background = texture;
            _that.scene.environment = texture;
        });
    }

    merge(object) {
        const geometries = [];
        const materialArr = [];
        object.traverse((obj) => {
            const child = obj;
            if (child.isMesh) {
                const geo = child.geometry.clone();
                if (Array.isArray(child.material)) {
                    child.material = child.material[0];
                }
                materialArr.push(child.material);
                geo.index = null;
                child.updateWorldMatrix(true, true);
                geo.applyMatrix4(child.matrixWorld);
                geometries.push(geo);
            }
        });
        // console.log(geometries);
        const buffergeo = BufferGeometryUtils.mergeBufferGeometries(
            geometries,
            true
        );
        // console.log(buffergeo);
        const mesh = new THREE.Mesh(buffergeo, materialArr);

        return mesh;
    }
    add3DBox(_modal) {
        const geometry = new THREE.BoxGeometry(1, 1, 1);
        const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
        const cube = new THREE.Mesh(geometry, material);
        this.scene.add(cube);
    }

    deleteObject(_object) {
        this.transformControls.detach();
        _object.removeFromParent();
        // if (_object) {
        //     _object.traverse(function (item) {
        //         if (item instanceof THREE.Mesh) {
        //             if (Array.isArray(item.material)) {
        //                 item.material.forEach(a => {
        //                     a.dispose()
        //                 })
        //             } else {
        //                 item.material.dispose() // 删除材质
        //             }
        //             item.geometry.dispose() // 删除几何体
        //         }
        //         item = null
        //     })
        //     // 删除场景对象scene的子对象group
        //     this.scene.remove(_object);
        // }
    }

    getAllObject() {
        let _objects = [];
        let _name = '';
        this.scene.traverse(function (child) {
            if (child instanceof THREE.Mesh) {
                _name = child.name;
                if (_name == 'X' || _name == 'Y' || _name == 'Z'
                    || _name == 'XY' || _name == 'XZ' || _name == 'YZ'
                    || _name == 'XYZ' || _name == 'E' || _name == 'XYZE'
                    || _name == 'START' || _name == 'END') {
                    return;
                }
                _objects.push(child);
            }
        });
        return _objects;
    }

    exportScene() {
        return JSON.stringify(this.scene.toJSON());
    }
}