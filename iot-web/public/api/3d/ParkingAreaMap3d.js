
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { DRACOLoader } from 'three/addons/loaders/DRACOLoader.js';
import { RGBELoader } from 'three/addons/loaders/RGBELoader.js';
import { TextGeometry } from 'three/addons/geometries/TextGeometry.js';
import { FontLoader } from 'three/addons/loaders/FontLoader.js';
export default class ParkingAreaMap3d {

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
        this.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
        // position and point the camera to the center of the scene
        this.camera.position.z = 5;
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
        this.controls.enableRotate = false; 
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

    addText(_objName, _text, _position) {
        let _that = this;
        const loader = new FontLoader();
        loader.load('/js/three/examples/fonts/helvetiker_regular.typeface.json', function (font) {
            const geometry = new TextGeometry(_text, {
                font: font,
                size: 0.15,
                height: 0.05
            });

            let material = new THREE.MeshBasicMaterial({ color: 0xffffff , transparent: true, opacity: 1 }); // 创建材质  
            let mesh = new THREE.Mesh(geometry, material); // 创建网格并添加到场景中  
            mesh.name = _objName;
            //mesh.scale.set(10, 10, 10)
            mesh.position.set(_position.x, _position.y+0.6, _position.z);
            //mesh.rotateX(270 / 180 * Math.PI)
            _that.scene.add(mesh); // 将锥形对象添加到场景中 
        });
    }

    createParkingSpace(x, y, z, parkingSpaceSize) {
        console.log(parkingSpaceSize)
        const parkingSpaceDepth = 0.1; // 车位的厚度  
        let _color =  0x00FF00;
        if(parkingSpaceSize.color){
            _color = parkingSpaceSize.color;
        }
        const parkingSpaceMaterial = new THREE.MeshBasicMaterial({ color: _color }); // 灰色材质  
        const geometry = new THREE.BoxGeometry(parkingSpaceSize.width, parkingSpaceSize.height, parkingSpaceDepth);
        const mesh = new THREE.Mesh(geometry, parkingSpaceMaterial);
        mesh.position.set(x, y, z); // 设置车位位置  
        this.scene.add(mesh); // 将车位添加到场景中 
        return mesh; // 返回车位对象以便后续管理  
    }

    /**
     * 初始化车位
     * @param {车位列表} pSpaces 
     */
    initParkingSpace(pSpaces) {
        this.resetScene();

        let sWidth = 0.5;
        let sHeight = 1;
        let lineSpaceLenght = 50;
        let roadHeight = 1;
        // 创建多个车位  
        const spaceBetween = 0.1; // 车位之间的间隔  

        //如果车位数没有超过lineSpaceLenght 那么lineSpaceLenght 修改为车位数
        if(pSpaces.length < lineSpaceLenght && pSpaces.length > 20){
            lineSpaceLenght = 20;
        }else if(pSpaces.length < lineSpaceLenght){
            lineSpaceLenght = pSpaces.length;
        }

       
        

        let pWidth = sWidth * lineSpaceLenght + lineSpaceLenght * spaceBetween + roadHeight *2;
        let totalLines = Math.ceil(pSpaces.length / (2 * lineSpaceLenght));
        let pHeight = sHeight * 2 * totalLines + roadHeight * (totalLines + 1);

        //todo 添加一个车位平面
        // 创建一个停车场平面  
        const planeGeometry = new THREE.PlaneGeometry(pWidth, pHeight); // 10x10的平面  
        const planeMaterial = new THREE.MeshBasicMaterial({ color: 0x808080 }); // 灰色材质  
        const parkingLotPlane = new THREE.Mesh(planeGeometry, planeMaterial);


        // 将停车场平面添加到场景中  
        this.scene.add(parkingLotPlane);

        let _x = 0;
        let _y = pHeight / 2 - sHeight / 2 - sHeight;
        let _textX = 0;
        let _textY = _y + sHeight/4 - spaceBetween *2 ;
        for (let i = 0; i < pSpaces.length; i++) {
            _x = -pWidth / 2 + (i%lineSpaceLenght) * (sWidth + spaceBetween) + sWidth/2 + roadHeight;
            _textX = _x;
            if (i%lineSpaceLenght == 0 && i != 0 ) {
                _y -=  (sHeight+spaceBetween);
                _textY = _y - sHeight/4 - sHeight - spaceBetween *2 ;
            }
            if(i% (2*lineSpaceLenght) == 0 && i != 0){
                _y -= roadHeight;
                _textY  = _y + sHeight/4 - spaceBetween *2 ;
            }
            
            console.log(pWidth, pHeight, _x, _y);
            this.createParkingSpace(_x, _y, 0, {
                width: sWidth,
                height: sHeight,
                color:pSpaces[i].psColor
            }); // 创建车位并设置位置  
            if(pSpaces[i].num.length>1 ){
                _textX = _x -sWidth/2
            }
            this.addText(pSpaces[i].num,pSpaces[i].num+"",{
                x:_textX,
                y:_textY,
                z:0
            }) 
        }

    }
}