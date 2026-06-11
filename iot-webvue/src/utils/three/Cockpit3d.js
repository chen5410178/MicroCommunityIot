import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader.js'
import { RGBELoader } from 'three/examples/jsm/loaders/RGBELoader.js'
import { TextGeometry } from 'three/examples/jsm/geometries/TextGeometry.js'
import { FontLoader } from 'three/examples/jsm/loaders/FontLoader.js'

export default class Cockpit3d {

  constructor(_element) {
    this.shapes = []
    this._initScene(_element)
  }

  initScene() {
    this.scene = new THREE.Scene()
  }

  initCamera() {
    this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000)
    this.camera.position.set(-30, 40, 30)
    this.camera.lookAt(this.scene.position)
  }

  initRenderer(_element) {
    this.renderer = new THREE.WebGLRenderer()
    this.renderer.setClearColor(new THREE.Color(0x87CEEB))
    this.renderer.setSize(window.innerWidth, window.innerHeight)
    this.renderer.antialias = true
    this.renderer.alpha = true
    this.renderer.precision = 'mediump'
    this.container = document.getElementById(_element)
    this.container.appendChild(this.renderer.domElement)
  }

  initControls() {
    this.controls = new OrbitControls(this.camera, this.renderer.domElement)
    this.controls.minPolarAngle = 0
    this.controls.maxPolarAngle = 75 / 180 * Math.PI
    this.controls.minDistance = 200
    this.controls.maxDistance = 500
  }

  initLight() {
    this.light = new THREE.SpotLight(0xffffff)
    this.light.position.set(-300, 600, -400)
    this.light.castShadow = true
    this.scene.add(this.light)
    this.scene.add(new THREE.AmbientLight(0x404040))
  }

  update() {
    this.controls.update()
    this.upDownShap()
  }

  _initScene(_element) {
    this.initScene()
    this.initCamera()
    this.initRenderer(_element)
    this.initLight()
    this.initControls()

    let _that = this
    function animate() {
      requestAnimationFrame(animate)
      _that.renderer.render(_that.scene, _that.camera)
      _that.update(_that)
    }
    animate()

    window.addEventListener('click', function (event) {
      _that.onMouseDblclick(event, _that)
    }, false)
    window.addEventListener('mousemove', function (event) {
      _that.onMouseMove(event, _that)
    }, false)
  }

  addGltfObject(_modal) {
    const loader = new GLTFLoader()
    const dracoLoader = new DRACOLoader()
    dracoLoader.setDecoderPath('/js/three/examples/jsm/libs/draco/')
    loader.setDRACOLoader(dracoLoader)
    let _that = this
    return new Promise((resolve, reject) => {
      loader.load(
        _modal.path,
        function (gltf) {
          gltf.scene.traverse(function (child) {
            if (child.isMesh) {
              child.material.emissive = child.material.color
              child.material.emissiveMap = child.material.map
            }
          })
          gltf.scene.scale.set(1, 1, 1)
          _that.scene.add(gltf.scene)
          resolve()
        },
        function (xhr) {
          console.log((xhr.loaded / xhr.total * 100) + '% loaded')
        },
        function (error) {
          console.log('An error happened', error)
          reject(error)
        }
      )
    })
  }

  addJpgBackgroup(_modal) {
    const texture = new THREE.TextureLoader().load(_modal.path)
    texture.mapping = THREE.EquirectangularReflectionMapping
    texture.magFilter = THREE.LinearFilter
    texture.minFilter = THREE.LinearMipmapLinearFilter
    texture.colorSpace = THREE.SRGBColorSpace
    this.scene.background = texture
    this.scene.environment = texture
  }

  addHdrBackgroup(_modal) {
    const rgbeLoader = new RGBELoader()
    let _that = this
    rgbeLoader.loadAsync(_modal.path).then((texture) => {
      texture.mapping = THREE.EquirectangularReflectionMapping
      _that.scene.background = texture
      _that.scene.environment = texture
    })
  }

  clearThree(obj) {
    while (obj.children.length > 0) {
      this.clearThree(obj.children[0])
      obj.remove(obj.children[0])
    }
    if (obj.geometry) obj.geometry.dispose()
    if (obj.material) {
      Object.keys(obj.material).forEach(prop => {
        if (!obj.material[prop]) return
        if (typeof obj.material[prop].dispose === 'function')
          obj.material[prop].dispose()
      })
      obj.material.dispose()
    }
  }

  resetScene() {
    this.clearThree(this.scene)
  }

  setSelect3DObject(_callback) {
    this.selectCallBack = _callback
  }

  onMouseMove(event, _that) {
    let intersects = _that.getIntersects(event, _that)
    if (!intersects || intersects.length === 0) {
      return
    }
    let _name = ''
    document.body.style.cursor = 'default'
    for (let _index = 0; _index < intersects.length; _index++) {
      _name = intersects[_index].object.name
      if (_name.endsWith('_focus') || _name.endsWith('_text')) {
        document.body.style.cursor = 'pointer'
        break
      }
    }
  }

  onMouseDblclick(event, _that) {
    let intersects = _that.getIntersects(event, _that)
    let _name = ''
    if (intersects.length !== 0) {
      for (let _index = 0; _index < intersects.length; _index++) {
        _name = intersects[_index].object.name
        if (_name == 'X' || _name == 'Y' || _name == 'Z'
          || _name == 'XY' || _name == 'XZ' || _name == 'YZ'
          || _name == 'XYZ' || _name == 'E' || _name == 'XYZE'
          || _name == 'START' || _name == 'END') {
          continue
        }
        if (intersects[_index].object.type === 'Mesh') {
          _that.selectObject = intersects[_index].object
          if (_that.selectCallBack) {
            _that.selectCallBack(_that.selectObject)
          }
          break
        }
      }
    } else {
      console.log('未选中 Mesh!')
    }
  }

  getIntersects(event, _that) {
    if (event.target.tagName != 'CANVAS') {
      return []
    }
    event.preventDefault()
    let rayCaster = new THREE.Raycaster()
    let mouse = new THREE.Vector2()
    mouse.x = ((event.clientX - _that.container.getBoundingClientRect().left) / _that.container.offsetWidth) * 2 - 1
    mouse.y = -((event.clientY - _that.container.getBoundingClientRect().top) / _that.container.offsetHeight) * 2 + 1
    rayCaster.setFromCamera(mouse, _that.camera)
    let intersects = rayCaster.intersectObjects(_that.scene.children, true)
    return intersects
  }

  getObjectByName(_objName) {
    return this.scene.getObjectByName(_objName)
  }

  addConicalShape(_objName) {
    let geometry = new THREE.ConeGeometry(8, 15, 20)
    let material = new THREE.MeshBasicMaterial({ color: 0xffffff, transparent: true, opacity: 0.7 })
    let cone = new THREE.Mesh(geometry, material)
    cone.name = _objName
    cone.rotation.x = Math.PI
    this.scene.add(cone)
  }

  addText(_objName, _text, _position) {
    let _that = this
    const loader = new FontLoader()
    loader.load('/js/three/examples/fonts/helvetiker_regular.typeface.json', function (font) {
      const geometry = new TextGeometry(_text, {
        font: font,
        size: 6,
        height: 1,
        curveSegments: 8,
        bevelEnabled: true,
        bevelThickness: 1,
        bevelSize: 0.1,
        bevelSegments: 2
      })
      let material = new THREE.MeshBasicMaterial({ color: 0xc17e14, transparent: true, opacity: 0.8 })
      let mesh = new THREE.Mesh(geometry, material)
      mesh.name = _objName
      mesh.position.set(_position.x, _position.y, _position.z)
      mesh.rotateX(270 / 180 * Math.PI)
      _that.scene.add(mesh)
    })
  }

  getObjectPosition(targetObject) {
    let targetCenter = new THREE.Vector3()
    let box = new THREE.Box3().setFromObject(targetObject)
    targetCenter.x = (box.max.x + box.min.x) / 2
    targetCenter.y = box.min.y
    targetCenter.z = (box.max.z + box.min.z) / 2
    return { x: targetCenter.x, y: targetCenter.y, z: targetCenter.z }
  }

  getObjectHeight(targetObject) {
    let box = new THREE.Box3().setFromObject(targetObject)
    let height = box.max.y - box.min.y
    return height
  }

  addShap(_object) {
    this.shapes.push({
      object: _object,
      y: _object.position.y
    })
  }

  upDownShap() {
    if (!this.shapes || this.shapes.length < 1) {
      return
    }
    let speed = 1.5
    let amplitude = 5
    this.shapes.forEach(shape => {
      let newYPosition = shape.y + amplitude * Math.sin(speed * Date.now() / 1000)
      shape.object.position.y = newYPosition
    })
  }

  lookAtObject(_object) {
    let _pos = this.getObjectPosition(_object)
    this.camera.position.set(_pos.x, _pos.y, _pos.z)
    this.camera.lookAt(_object.position)
  }
}
