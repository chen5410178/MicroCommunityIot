import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { DragControls } from 'three/addons/controls/DragControls.js';
import Stats from 'three/addons/libs/stats.module.js';
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

    const geometry = new THREE.BoxGeometry(1, 1, 1);

    const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
    const cube = new THREE.Mesh(geometry, material);

    _that.scene.add(cube);

    const geometryP = new THREE.PlaneGeometry(10, 10, 10);
    const materialP = new THREE.MeshPhongMaterial({
        color: 0xffffff
    });
    const plane = new THREE.Mesh(geometryP, materialP);
    plane.position.set(0, 0, 0);
    plane.rotation.x = -Math.PI / 2;
    scene.add(plane);

    let moveObjectsMeshArray = [];
    moveObjectsMeshArray.push(cube);
    moveObjectsMeshArray.push(plane);


    let dragControls = new DragControls(moveObjectsMeshArray, camera, renderer.domElement);
    // 拖拽事件监听
    dragControls.addEventListener('dragstart', () => {
        // 设置控制器不可操作
        controls.enabled = false
    })
    dragControls.addEventListener('dragend', () => {
        controls.enabled = true
    })



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
 * 添加
 */
export function addBoxGeometry(_elementName, _that) {
    const geometry = new THREE.BoxGeometry(1, 1, 1);
    const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
    const cube = new THREE.Mesh(geometry, material);
    _that.scene.add(cube);

    let moveObjectsMeshArray = [];
    moveObjectsMeshArray.push(cube);
    let container = document.getElementById(_elementName);
    let mRayCasterMoveObjectsWrapper =
        new RayCasterMoveObjectsWrapper(moveObjectsMeshArray, container, [], () => {
            // 移动开始的时候，停止OrbitControls轨道控制器功能
            controls.enabled = false;
        },
            () => {
                // 移动结束的时候，使能OrbitControls轨道控制器功能 
                controls.enabled = true;
            });

    // 使能点击移动功能
    mRayCasterMoveObjectsWrapper.enableMouseMoveObjs();

}

