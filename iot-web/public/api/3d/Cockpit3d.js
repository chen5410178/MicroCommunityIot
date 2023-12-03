
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'


export default class Cockpit3d {

    constructor(_element) {
        this._initScene(_element);
    }


    /**
     * 初始化场景
     */
    initScene() {
        this.scene = new THREE.Scene();
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
        this.renderer.setSize(window.innerWidth, window.innerHeight);
        this.renderer.antialias = true;

        this.renderer.alpha = true;

        this.renderer.precision = 'mediump'
        // add the output of the renderer to the html element
        this.container = document.getElementById(_element);
        this.container.appendChild(this.renderer.domElement);
    }

    initControls() {
        this.controls = new OrbitControls(this.camera, this.renderer.domElement);
        // 设置控制器阻尼，让控制器更有真是效果，必须在动画循环里调用update（）
        //this.controls.enableDamping = true;
        this.controls.minPolarAngle = 0;
        this.controls.maxPolarAngle = 75 / 180 * Math.PI;
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

    }

    _initScene(_element) {

        this.initScene();
        this.initCamera();
        this.initRenderer(_element);
        this.initLight();
        this.initControls();

        let _that = this;
        function animate() {
            requestAnimationFrame(animate);
            _that.renderer.render(_that.scene, _that.camera);
            _that.update(_that);
        }
        animate();
    }

    addGltfObject(_modal) {
        const loader = new GLTFLoader();
        const dracoLoader = new DRACOLoader();
        dracoLoader.setDecoderPath('/js/three/examples/jsm/libs/draco/');
        loader.setDRACOLoader(dracoLoader);
        let _that = this;
        loader.load(
            _modal.path,
            function (gltf) {
                //需要添加的部分
                gltf.scene.traverse(function (child) {
                    if (child.isMesh) {
                        child.material.emissive = child.material.color;
                        child.material.emissiveMap = child.material.map;
                    }
                });
                _that.scene.add(gltf.scene);
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
        const texture = new THREE.TextureLoader().load(_modal.path);
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
}