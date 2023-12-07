//todo 读取文件
const config = require('./vcJs.json');
//const fs = require('fs');
const fs = require('fs-extra');
const path = require('path');
const jsdom = require("jsdom");
const { error } = require('console');
const { JSDOM } = jsdom;

let document = null;
/**
 * 读取src 下的html文件
 */
function readDir(dir, _extname) {
    fs.readdir(dir, (err, files) => {
        if (err) {
            console.error('无法读取文件夹:', err);
            return;
        }
        files.forEach(file => {
            const filePath = path.join(dir, file);
            fs.stat(filePath, (err, stats) => {
                if (err) {
                    console.error('无法获取文件状态:', err);
                    return;
                }

                if (stats.isDirectory()) {
                    readDir(filePath, _extname); // 递归读取子文件夹
                } else if (path.extname(file).toLowerCase() === _extname) {
                    fs.readFile(filePath, 'utf8', (err, data) => {
                        const dom = new JSDOM(data);
                        document = dom.window.document;
                        let _pageData = parseVcCreate(filePath);
                        _pageData.then(_div => {
                            createFile(filePath.replaceAll("public\\", ""), _div);
                        })
                    });
                }
            });
        });
    });
}

function readTemplateHtml(dir, _extname){
    fs.readdir(config.src, (err, files) => {
        if (err) {
            console.error('无法读取文件夹:', err);
            return;
        }
        files.forEach(file => {
            const filePath = path.join(dir, file);
            fs.stat(filePath, (err, stats) => {
                if (err) {
                    console.error('无法获取文件状态:', err);
                    return;
                }
    
                if (stats.isDirectory()) {
                } else if (path.extname(file).toLowerCase() === _extname) {
                    fs.readFile(filePath, 'utf8', (err, data) => {
                        const dom = new JSDOM(data);
                        document = dom.window.document;
                        let _pageData = parseTempateVcCreate(filePath);
                        _pageData.then(_div => {
                            createFile(filePath, _div);
                        })
                    });
                }
            });
        });
    });
}

function createFile(fileName, content) {
    const fullPath = path.join(config.dist, fileName);

    const directoryPath = path.dirname(fullPath);

    // 检查目录是否存在，如果不存在则创建目录
    if (!fs.existsSync(directoryPath)) {
        fs.mkdirSync(directoryPath, { recursive: true });
    }

    // 创建文件
    fs.writeFileSync(fullPath, content);
}

/**
 * 拷贝目录
 * @param {*} source 
 * @param {*} destination 
 */
function copyDirectory(source, destination) {
    if (destination.indexOf('pages') > 0) {
        return;
    }
    // if(destination.indexOf('components') >0){
    //     return;
    // }
    // 创建目标目录
    fs.mkdirSync(destination, { recursive: true });

    // 读取源目录中的所有文件和子目录
    const files = fs.readdirSync(source);

    // 遍历文件列表
    for (const file of files) {
        // 构建源文件路径和目标文件路径
        const sourcePath = path.join(source, file);
        const destinationPath = path.join(destination, file);

        // 如果是文件，则拷贝文件内容
        if (fs.lstatSync(sourcePath).isFile()) {
            fs.writeFileSync(destinationPath, fs.readFileSync(sourcePath));
        } else if (fs.lstatSync(sourcePath).isDirectory()) {
            // 如果是目录，则递归拷贝子目录
            copyDirectory(sourcePath, destinationPath);
        }
    }
}

uuid = function () {
    let s = [];
    let hexDigits = "0123456789abcdef";
    for (let i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    let uuid = s.join("");
    return uuid;
};

let VcTree = function (_vcCreate, _html, _nodeLocation) {
    let o = new Object();
    o.treeId = uuid();
    o.vcCreate = _vcCreate;
    o.html = _html;
    o.js = "";
    o.css = "";
    o.vcSubTree = [];
    o.nodeLocation = _nodeLocation;
    o.putSubTree = function (_vcSubTree) {
        o.vcSubTree.push(_vcSubTree);
    };
    o.setHtml = function (_html) {
        o.html = _html;
    };
    o.setJs = function (_js) {
        o.js = _js;
    };
    o.setCss = function (_css) {
        o.css = _css;
    };
    o.setLocation = function (_location) {
        o.nodeLocation = _location;
    };
    return o;
};

/**
     * 加载组件
     * 异步去服务端 拉去HTML 和 js
     */
loadComponent = async function (_componentName, _tree) {
    if (_componentName && _componentName.lastIndexOf('/') > 0) {
        _componentName = _componentName + '/' + _componentName.substring(_componentName.lastIndexOf('/') + 1, _componentName.length);
    }

    console.log('加载组件名称', _componentName);

    let _domain = 'components';
    let filePath = '';

    if (_tree.vcCreate.hasAttribute("domain")) {
        _domain = _tree.vcCreate.getAttribute("domain");
    }
    if (_componentName.startsWith('/pages')) { //这里是为了处理 pages 页面
        filePath = _componentName;
    } else { //这里是为了处理组件
        filePath = '/' + _domain + '/' + _componentName;
    }
    let htmlFilePath = filePath + ".html";
    let jsFilePath = filePath + ".js";
    let _htmlBody = "";
    let _jsBody = "";
    try {
        _htmlBody = fs.readFileSync(config.src + htmlFilePath, 'utf-8');
        _jsBody = fs.readFileSync(config.src + jsFilePath, 'utf-8');
    } catch (error) {
        return null;
    }


    //处理命名空间
    _htmlBody = dealHtmlNamespace(_tree, _htmlBody);

    //处理 js
    _jsBody = dealJs(_tree, _jsBody);
    _jsBody = dealJsAddComponentCode(_tree, _jsBody);
    //处理命名空间
    _jsBody = dealJsNamespace(_tree, _jsBody);

    //处理侦听
    _jsBody = dealHtmlJs(_tree, _jsBody);
    // 
    // _tmpJsBody = '<script type="text/javascript">//<![CDATA[\n' + _jsBody + '//]]>\n</script>';

    let htmlComponentDom = new JSDOM(_htmlBody);
    let htmlComponentDoc = htmlComponentDom.window.document;

    _tree.setHtml(htmlComponentDoc);
    _tree.setJs(_jsBody);
    return htmlComponentDoc;
};

/**
    * 处理 命名空间html
    */
dealHtmlNamespace = function (_tree, _html) {

    let _componentVcCreate = _tree.vcCreate;
    if (!_componentVcCreate.hasAttribute('namespace')) {
        return _html;
    }

    let _namespaceValue = _componentVcCreate.getAttribute("namespace");

    _html = _html.replace(/this./g, _namespaceValue + "_");

    _html = _html.replace(/(id)( )*=( )*'/g, "id='" + _namespaceValue + "_");
    _html = _html.replace(/(id)( )*=( )*"/g, 'id="' + _namespaceValue + '_');
    return _html;
};

/**
     * 处理命名空间js
     */
dealHtmlJs = function (_tree, _js) {
    let _componentVcCreate = _tree.vcCreate;
    if (!_componentVcCreate.hasAttribute('namespace')) {
        return _js;
    }

    let _namespaceValue = _componentVcCreate.getAttribute("namespace");
    _js = _js.replace(/this./g, "vc.component." + _namespaceValue + "_");
    _js = _js.replace(/(\$)( )*(\()( )*'#/g, "\$('#" + _namespaceValue + "_");

    _js = _js.replace(/(\$)( )*(\()( )*"#/g, "\$(\"#" + _namespaceValue + "_");

    //将 监听也做优化
    _js = _js.replace(/(vc.on)\('/g, "vc.on('" + _namespaceValue + "','");
    _js = _js.replace(/(vc.on)\("/g, "vc.on(\"" + _namespaceValue + "\",\"");
    return _js;
}
/**
 * 处理js
 */
dealJs = function (_tree, _js) {
    //在js 中检测propTypes 属性
    if (_js.indexOf("propTypes") < 0) {
        return _js;
    }

    let _componentVcCreate = _tree.vcCreate;

    //解析propTypes信息
    let tmpProTypes = _js.substring(_js.indexOf("propTypes"), _js.length);
    tmpProTypes = tmpProTypes.substring(tmpProTypes.indexOf("{") + 1, tmpProTypes.indexOf("}")).trim();

    if (!tmpProTypes) {
        return _js;
    }

    tmpProTypes = tmpProTypes.indexOf("\r") > 0 ? tmpProTypes.replace("\r/g", "") : tmpProTypes;

    let tmpType = tmpProTypes.indexOf("\n") > 0 ?
        tmpProTypes.split("\n") :
        tmpProTypes.split(",");
    let propsJs = "\nlet $props = {};\n";
    for (let typeIndex = 0; typeIndex < tmpType.length; typeIndex++) {
        let type = tmpType[typeIndex];
        if (!type || type.indexOf(":") < 0) {
            continue;
        }
        let types = type.split(":");
        let attrKey = "";
        if (types[0].indexOf("//") > 0) {
            attrKey = types[0].substring(0, types[0].indexOf("//"));
        }
        attrKey = types[0].replace(" ", "");
        attrKey = attrKey.replace("\n", "")
        attrKey = attrKey.replace("\r", "").trim();
        if (!_componentVcCreate.hasAttribute(attrKey) && types[1].indexOf("=") < 0) {
            let componentName = _componentVcCreate.getAttribute("path");
            throw "组件[" + componentName + "]未配置组件属性" + attrKey;
        }
        let vcType = _componentVcCreate.getAttribute(attrKey);
        if (!_componentVcCreate.hasAttribute(attrKey) && types[1].indexOf("=") > 0) {
            vcType = dealJsPropTypesDefault(types[1]);
        } else if (types[1].indexOf("vc.propTypes.string") >= 0) {
            vcType = "'" + vcType + "'";
        }
        propsJs = propsJs + "$props." + attrKey + "=" + vcType + ";\n";
    }

    //将propsJs 插入到 第一个 { 之后
    let position = _js.indexOf("{");
    if (position < 0) {
        let componentName = _componentVcCreate.getAttribute("name");
        throw "组件" + componentName + "对应js 未包含 {}  ";
    }
    let newJs = _js.substring(0, position + 1);
    newJs = newJs + propsJs;
    newJs = newJs + _js.substring(position + 1, _js.length);
    newJs = newJs.replace('propTypes','pTypes');
    newJs = newJs.replaceAll('vc.propTypes.string','""');
    return newJs;
};

dealJsPropTypesDefault = function (typeValue) {
    let startPos = typeValue.indexOf("=") + 1;
    let endPos = typeValue.length;
    if (typeValue.indexOf(",") > 0) {
        endPos = typeValue.indexOf(",");
    } else if (typeValue.indexOf("//") > 0) {
        endPos = typeValue.indexOf("//");
    }

    return typeValue.substring(startPos, endPos);
};
/**
 * js 处理命名
 */
dealJsNamespace = function (_tree, _js) {

    //在js 中检测propTypes 属性
    let _componentVcCreate = _tree.vcCreate;

    if (_js.indexOf("vc.extends") < 0) {
        return _js;
    }
    let namespace = "";
    if (!_componentVcCreate.hasAttribute("namespace")) {
        namespace = 'default';
    } else {
        namespace = _componentVcCreate.getAttribute("namespace");
    }
    //     let propsJs = "\nlet $namespace='" + namespace.trim() + "';\n";
    //    let newJs = _js.replace('vc.extends',propsJs+"vc.extends");

    // //js对象中插入namespace 值
    // if(namespace == 'chooseEnterCommunity'){
    //     debugger;
    //     console.log(_js)
    // }
    let extPos = _js.indexOf("vc.extends");
    let tmpProTypes = _js.substring(extPos, _js.length);
    let pos = tmpProTypes.indexOf("{") + 1;
    _js = _js.substring(0, extPos) + tmpProTypes.substring(0, pos).trim() +
        "\nnamespace:'" + namespace.trim() + "',\n" + tmpProTypes.substring(pos, tmpProTypes.length);


    let propsJs = "\nlet $namespace='" + namespace.trim() + "';\n";
    let newJs = _js.replace('vc.extends', propsJs + "vc.extends");
    // let position = _js.indexOf("{");
    // let propsJs = "\nlet $namespace='" + namespace.trim() + "';\n";

    // let newJs = _js.substring(0, position + 1);
    // newJs = newJs + propsJs;
    // newJs = newJs + _js.substring(position + 1, _js.length);

    return newJs;
};

/**
 * 处理js 变量和 方法都加入 组件编码
 *
 * @param tag 页面元素
 * @param js  js文件内容
 * @return js 文件内容
 */
dealJsAddComponentCode = function (_tree, _js) {
    let _componentVcCreate = _tree.vcCreate;

    if (!_componentVcCreate.hasAttribute("code")) {
        return _js;
    }

    let code = _componentVcCreate.getAttribute("code");

    return _js.replace("@vc_/g", code);
}

findVcLabel = async function (_tree) {
    //查看是否存在子 vc:create 
    let _componentName = _tree.vcCreate.getAttribute('path');
    //console.log('_componentName', _componentName, _tree);
    if (!_componentName) {
        throw '组件未包含path 属性' + _tree.vcCreate.outerHTML;
    }
    //开始加载组件
    let _componentElement = await loadComponent(_componentName, _tree);
    //_tree.setHtml(_componentElement);

    //console.log('_componentElement>>', _componentElement)

    if (_componentElement) {
        let vcChildElements = _componentElement.getElementsByTagName('vc:create');
        if (vcChildElements.length > 0) {
            for (let _vcChildIndex = 0; _vcChildIndex < vcChildElements.length; _vcChildIndex++) {
                //console.log('vcChildElements', vcChildElements);
                let _tmpChildElement = vcChildElements[_vcChildIndex];
                let _subtree = new VcTree(_tmpChildElement, '', 2);
                let _vcCreateAttr = _componentElement.createAttribute('id');
                _vcCreateAttr.value = _subtree.treeId;
                _tmpChildElement.setAttributeNode(_vcCreateAttr);
                _tree.putSubTree(_subtree);
                await findVcLabel(_subtree);
            }
        }

    }
};

async function parseVcCreate(filePath) {

    let vcChildElements = document.getElementsByTagName('vc:create');
    //console.log('vcCreate', vcChildElements.length);
    let treeList = [];
    let _componentScript = [];
    if (vcChildElements.length > 0) {
        let _vcDiv = document.createElement('div');
        for (let _vcChildIndex = 0; _vcChildIndex < vcChildElements.length; _vcChildIndex++) {
            //console.log('vcChildElements', vcChildElements);
            let _tmpChildElement = vcChildElements[_vcChildIndex];
            let _subtree = new VcTree(_tmpChildElement, '', 2);
            let _vcCreateAttr = document.createAttribute('id');
            _vcCreateAttr.value = _subtree.treeId;
            _tmpChildElement.setAttributeNode(_vcCreateAttr);
            treeList.push(_subtree);
            await findVcLabel(_subtree);
        }
    }
    //todo 将本身的js 导入

    let _jsFilePath = filePath.replace('.html', '.js')
    try {
        let _jsBody = fs.readFileSync(_jsFilePath, 'utf-8');
        _componentScript.push(_jsBody);
    } catch (error) {
        console.error(error);
    }
    reader(treeList, _componentScript);

    let _js = "";
    _componentScript.forEach(_jsScript=>{
        _js += (_jsScript+";");
    })

    //_js += "vcFramework.initVue();vcFramework.initVcComponent();"

    // let _js = "let componentsJs=" + JSON.stringify(_componentScript).replaceAll('\n','')+";";

    // _js += `componentsJs.forEach(_js => {
    //     let script = document.createElement('script');
    //     script.setAttribute("type", "module");
    //     script.innerHTML = _js + ';'; // 替换为你的JavaScript代码
    //     document.body.appendChild(script);
    // });`


    createFile(_jsFilePath.replaceAll("public\\", ""), _js);

    vcChildElements = document.getElementsByTagName('vc:create');

    if (vcChildElements.length > 0) {
        for (let _vcChildIndex = 0; _vcChildIndex < vcChildElements.length; _vcChildIndex++) {
            //console.log('vcChildElements', vcChildElements);
            let _tmpChildElement = vcChildElements[_vcChildIndex];
            // 删除元素
            _tmpChildElement.parentNode.removeChild(_tmpChildElement);
        }
    }

    const documentString = document.documentElement.querySelector('body');
    return documentString.innerHTML;
}

async function parseTempateVcCreate(filePath) {

    let vcChildElements = document.getElementsByTagName('vc:create');
    //console.log('vcCreate', vcChildElements.length);
    let treeList = [];
    let _componentScript = [];
    if (vcChildElements.length > 0) {
        let _vcDiv = document.createElement('div');
        for (let _vcChildIndex = 0; _vcChildIndex < vcChildElements.length; _vcChildIndex++) {
            //console.log('vcChildElements', vcChildElements);
            let _tmpChildElement = vcChildElements[_vcChildIndex];
            let _subtree = new VcTree(_tmpChildElement, '', 2);
            let _vcCreateAttr = document.createAttribute('id');
            _vcCreateAttr.value = _subtree.treeId;
            _tmpChildElement.setAttributeNode(_vcCreateAttr);
            treeList.push(_subtree);
            await findVcLabel(_subtree);
        }
    }
    //todo 将本身的js 导入
    reader(treeList, _componentScript);

    _componentScript.forEach(_jsScript=>{
        let script = document.createElement('script');
        script.setAttribute("type", "module");
        script.innerHTML = _jsScript + ';'; // 替换为你的JavaScript代码
        document.body.appendChild(script);
    })

    vcChildElements = document.getElementsByTagName('vc:create');

    if (vcChildElements.length > 0) {
        for (let _vcChildIndex = 0; _vcChildIndex < vcChildElements.length; _vcChildIndex++) {
            //console.log('vcChildElements', vcChildElements);
            let _tmpChildElement = vcChildElements[_vcChildIndex];
            // 删除元素
            _tmpChildElement.parentNode.removeChild(_tmpChildElement);
        }
    }

    const documentString = document.documentElement.querySelector('body');
    return documentString.innerHTML;
}
reader = function (_treeList, _componentScript) {
    //console.log('_treeList', _treeList);
    for (let _treeIndex = 0; _treeIndex < _treeList.length; _treeIndex++) {
        let _tree = _treeList[_treeIndex];
        let _vcCreateEl = document.getElementById(_tree.treeId);

        console.log('_tree.html.documentElement', _tree.html.documentElement)

        if (_tree.html.documentElement && _vcCreateEl && _vcCreateEl.parentNode) {
            _vcCreateEl.parentNode.insertBefore(_tree.html.documentElement.querySelector('body').childNodes[0], _vcCreateEl)
        }

        //将js 脚本放到 组件 脚本中
        if (_tree.js) {
            _componentScript.push(_tree.js);
        }


        let _tmpSubTrees = _tree.vcSubTree;

        if (_tmpSubTrees != null && _tmpSubTrees.length > 0) {
            reader(_tmpSubTrees, _componentScript);
        }

    }
    /**
     * 执行 页面上的js 文件
     */
    let _tmpScripts = document.head.getElementsByTagName("script");
    let _tmpBody = document.getElementsByTagName('body');
    for (let _scriptsIndex = 0; _scriptsIndex < _tmpScripts.length; _scriptsIndex++) {
        let _tmpScript = _tmpScripts[_scriptsIndex];
        //console.log('_head 中 script', _tmpScript.outerHTML)
        let scriptObj = document.createElement("script");
        scriptObj.src = _tmpScript.src;
        //_tmpScript.parentNode.removeChild(_tmpScript);
        scriptObj.type = "text/javascript";
        _tmpBody[0].appendChild(scriptObj);
    }
};


copyDirectory(config.src, config.dist);

//todo 删除目标目录
//fs.remove(config.dist, (err) => {});
//todo 处理pages 
readDir(config.src + "/pages", ".html");

readTemplateHtml(config.src,".html");



