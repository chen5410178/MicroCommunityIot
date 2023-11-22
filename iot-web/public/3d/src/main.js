import * as THREE from '/three';
// 创建场景
const scene = new THREE.Scene();
// 创建相机
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
// 创建渲染器
const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);
// 创建几何体
const geometry = new THREE.BoxGeometry(1, 1, 1);
// 创建材质
const material = new THREE.MeshBasicMaterial({
    color: 0x00ff00
});
// 创建网格对象
const cube = new THREE.Mesh(geometry, material);
// 将网格对象加入场景
scene.add(cube);
// 设置相机位置
camera.position.z = 5;
// 渲染场景

function render() {
    requestAnimationFrame(render);
    //    cube.rotation.x += 0.01;
    //    cube.rotation.y += 0.01;

    renderer.render(scene, camera);
}
render();