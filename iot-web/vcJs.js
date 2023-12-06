//todo 读取文件
const config = require('./vcJs.json');
const fs = require('fs');
const path = require('path');
/**
 * 读取src 下的html文件
 */
function readDir(dir,_extname) {
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
            readDir(filePath,_extname); // 递归读取子文件夹
          } else if (path.extname(file).toLowerCase() === _extname) {
            fs.readFile(filePath, 'utf8', (err, data) => {
              if (err) {
                console.error('无法读取文件:', err);
                return;
              }
  
              console.log('文件:', filePath);
              //console.log('内容:\n', data);
              wirteFile(filePath.replace(config.src,""),data);
            });
          }
        });
      });
    });
  }

  function wirteFile(fileName,content){

    fs.writeFile(config.dist+fileName, content, (err) => {
        if (err) throw err;
        console.log('文件已被保存');
    });
  }

  readDir(config.src+"/pages",".html");