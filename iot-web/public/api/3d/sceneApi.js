import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { DragControls } from 'three/addons/controls/DragControls.js';
import Stats from 'three/addons/libs/stats.module.js';
import { OBJLoader } from 'three/addons/loaders/OBJLoader.js';
/**
 *  初始化场景
 * @param {元素} _elementName 
 * @returns 
 */
export function initScene(_elementName, _that) {
    let scene = new THREE.Scene();

    scene.fog = new THREE.Fog(0x111111, 150, 200);

    let camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);

    // create a render and set the size
    let renderer = new THREE.WebGLRenderer();
    renderer.setClearColor(new THREE.Color(0x87CEEB));
    renderer.setSize(window.innerWidth - 390, window.innerHeight);

    // show axes in the screen
    let axes = new THREE.AxesHelper(5);
    scene.add(axes);


    // position and point the camera to the center of the scene
    camera.position.set(-30, 40, 30);
    camera.lookAt(scene.position);

    // add the output of the renderer to the html element
    let container = document.getElementById(_elementName);
    container.appendChild(renderer.domElement);
    // 性能监测
    let stats = new Stats();
    stats.domElement.style.position = 'absolute';
    stats.domElement.style.left = '200px';
    container.appendChild(stats.dom);


    // 创建轨道控制器
    const controls = new OrbitControls(camera, renderer.domElement);
    // 设置控制器阻尼，让控制器更有真是效果，必须在动画循环里调用update（）
    controls.enableDamping = true;

    scene.add(new THREE.AmbientLight(0x222222));

    _that.camera = camera;
    _that.scene = scene;
    _that.renderer = renderer;
    _that.controls = controls;
    // 实现每一帧渲染一次场景和相机
    function render(time) {
        // 每一帧执行一次render函数
        requestAnimationFrame(render);
        controls.update();
        renderer.render(scene, camera);
        stats.update();
    }
    render();
    return scene;
}

/**
 * 导入模型
 */
export function add3DObject(_that, _modal) {

    const loader = new OBJLoader();

    loader.load(
        // resource URL
        _modal.path,
        // called when resource is loaded
        function (object) {
            object.position.set(0, 0, 0);
            _that.scene.add(object);

            //control(_that);
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

export function control(_that) {
    let moveObjects = [];
    _that.scene.traverse(function (child) {
        if (child instanceof THREE.Mesh) {
            moveObjects.push(child);
        }
    });
    let dragControls = new DragControls(moveObjects, _that.camera, _that.renderer.domElement);
    // 拖拽事件监听
    dragControls.addEventListener('dragstart', () => {
        // 设置控制器不可操作
        _that.controls.enabled = false
    })
    dragControls.addEventListener('dragend', () => {
        _that.controls.enabled = true
    })
}

