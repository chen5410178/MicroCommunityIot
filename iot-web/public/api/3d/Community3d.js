
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { DRACOLoader } from 'three/addons/loaders/DRACOLoader.js';
import { RGBELoader } from 'three/addons/loaders/RGBELoader.js';
import { TextGeometry } from 'three/addons/geometries/TextGeometry.js';
import { FontLoader } from 'three/addons/loaders/FontLoader.js';
import {latLngToThreejs} from 'api/3d/LatLngToThreejs.js';
export default class Community3d {

    constructor(_element) {
        this._initScene(_element);
        this.shapes = [];

        
    }


    /**
     * 初始化场景
     */
    initScene() {
        this.scene = new THREE.Scene();
        let axes = new THREE.AxesHelper(10);
        this.scene.add(axes);
        let helper = new THREE.GridHelper(50, 20, 0xCD3700, 0x4A4A4A);//网格线
        this.scene.add(helper);
    }
    /**
     * 初始化相机
     */
    initCamera() {
        this.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
        // position and point the camera to the center of the scene
        this.camera.position.y = 20;
        //this.camera.lookAt(this.scene.position);
    }

    /**
     * 初始化渲染器
     */
    initRenderer(_element) {
        // create a render and set the size
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setClearColor(new THREE.Color(0xFFFFFF));
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
        // 禁用旋转功能  
        this.controls.minPolarAngle = 0;
        this.controls.maxPolarAngle = 90 / 180 * Math.PI;
       
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

        this.upDownShap();
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

        // addEventListener('click', function (event) {
        //     _that.onMouseDblclick(event, _that)
        // }, false);
        // window.addEventListener('mousemove', function (event) {
        //     _that.onMouseMove(event, _that)
        // }, false);


    }



    getObjectByName(_objName) {
        return this.scene.getObjectByName(_objName);
    }



   

    getObjectPosition(targetObject) {
        let targetCenter = new THREE.Vector3();
        // targetObject.updateMatrixWorld(true); // 确保目标物体的世界矩阵是最新的  
        // targetObject.getWorldPosition(targetCenter); // 将目标物体的世界位置存储到 targetCenter 中  
        // // 将源物体的位置设置为目标物体的中心位置  
        // console.log(targetCenter);
        let box = new THREE.Box3().setFromObject(targetObject);
        targetCenter.x = (box.max.x + box.min.x) / 2;
        targetCenter.y = box.min.y;
        targetCenter.z = (box.max.z + box.min.z) / 2;

        return { x: targetCenter.x, y: targetCenter.y, z: targetCenter.z };
    }

    getObjectHeight(targetObject) {
        let box = new THREE.Box3().setFromObject(targetObject);
        // 获取包围盒的高度  
        let height = box.max.y - box.min.y;
        return height;
    }

    upDownShap() {
        if (!this.shapes || this.shapes.length < 1) {
            return;
        }
        // 设置移动的速度和幅度  
        let speed = 1.5; // 移动速度  
        let amplitude = 5; // 移动幅度  
        this.shapes.forEach(shape => {
            let newYPosition = shape.y + amplitude * Math.sin(speed * Date.now() / 1000);
            shape.object.position.y = newYPosition;

        })
    }

    lookAtObject(_object) {
        // 将相机移动到物体位置并面对物体  
        let _pos = this.getObjectPosition(_object);
        this.camera.position.set(_pos.x, _pos.y, _pos.z);
        this.camera.lookAt(_object.position);
    }

    clearThree(obj) {
        while (obj.children.length > 0) {
            this.clearThree(obj.children[0])
            obj.remove(obj.children[0]);
        }
        if (obj.geometry) obj.geometry.dispose()

        if (obj.material) {
            //in case of map, bumpMap, normalMap, envMap ...
            Object.keys(obj.material).forEach(prop => {
                if (!obj.material[prop])
                    return
                if (typeof obj.material[prop].dispose === 'function')
                    obj.material[prop].dispose()
            })
            obj.material.dispose()
        }
    }

    resetScene() {
        this.clearThree(this.scene);
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

    addGltfObject(_modal) {
        const loader = new GLTFLoader();
        const dracoLoader = new DRACOLoader();
        dracoLoader.setDecoderPath('/js/three/examples/jsm/libs/draco/');
        loader.setDRACOLoader(dracoLoader);
        let _that = this;
        return new Promise((resolve, reject) => {
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
                    gltf.scene.position.set(_modal.x, _modal.y, _modal.z);
                    gltf.scene.scale.set(0.1,0.04,0.1);
                    _that.scene.add(gltf.scene);
                    resolve();
                },
                // called while loading is progressing
                function (xhr) {
                    console.log((xhr.loaded / xhr.total * 100) + '% loaded');
                },
                // called when loading has errors
                function (error) {
                    console.log('An error happened', error);
                    reject(error);
                }
            );
        })

    }

    /**
     * 初始化车位
     * @param {车位列表} pSpaces 
     */
    initFloors(_floors) {
        this.resetScene();

        this.addJpgBackgroup({
            path:'/glb/sky.jpg'
        })

        let _community = vc.getCurrentCommunity();

        if(!_community.lat){
            vc.showToast('未设置小区经度');
            return; 
        }

        let _that = this;

        _that.addGltfObject({
            path:'/glb/floor.glb',
            x:0,
            y:0,
            z:0
        })
        _floors.forEach(_f=>{
            if(!_f.lat){
                return;
            }
            let _latLng = latLngToThreejs(_f.lat,_f.lng,_community);
            console.log(_latLng)
            _that.addGltfObject({
                path:'/glb/floor.glb',
                x:_latLng.x,
                y:_latLng.y,
                z:_latLng.z
            })
        })


    }
}