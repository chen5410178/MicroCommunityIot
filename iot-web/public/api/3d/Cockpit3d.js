
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { DRACOLoader } from 'three/addons/loaders/DRACOLoader.js';
import { RGBELoader } from 'three/addons/loaders/RGBELoader.js';

export default class Cockpit3d {

    constructor(_element) {
        this._initScene(_element);
        this.shapes = [];
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
        this.controls.minDistance = 200;
        this.controls.maxDistance = 500; // 相机位置与观察目标点最大值
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
                    gltf.scene.scale.set(1, 1, 1);
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

    getObjectByName(_objName) {
        return this.scene.getObjectByName(_objName);
    }

    addConicalShape(_objName) {
        let geometry = new THREE.ConeGeometry(8, 15, 20); // 第三个参数是分段数，可以根据需要调整  

        // 创建材质  
        let material = new THREE.MeshBasicMaterial({ color: 0xffffff, transparent: true, opacity: 0.7 }); // 绿色  

        // 创建锥形对象  
        let cone = new THREE.Mesh(geometry, material);
        cone.name = _objName;
        cone.rotation.x = Math.PI;
        this.scene.add(cone); // 将锥形对象添加到场景中  
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

    addShap(_object) {

        this.shapes.push({
            object: _object,
            y: _object.position.y
        });
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
}