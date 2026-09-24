const isProd = process.env.NODE_ENV === 'production'
const outputDir = process.env.OUTPUT_DIR || 'iotWeb'
let CompressionWebpackPlugin = null
try {
  CompressionWebpackPlugin = require('compression-webpack-plugin')
} catch (e) {
  CompressionWebpackPlugin = null
}

function buildCompressPlugins() {
  if (!isProd || !CompressionWebpackPlugin) return []
  return [
    new CompressionWebpackPlugin({
      filename: '[path][base].gz',
      algorithm: 'gzip',
      test: /\.(js|css|html|svg)$/,
      threshold: 10240,
      minRatio: 0.8
    }),
    new CompressionWebpackPlugin({
      filename: '[path][base].br',
      algorithm: 'brotliCompress',
      test: /\.(js|css|html|svg)$/,
      threshold: 10240,
      minRatio: 0.8,
      compressionOptions: { level: 9 }
    })
  ]
}

module.exports = {
	outputDir: outputDir, // ✅ 使用环境变量作为输出目录
  devServer: {
    port: 3002,
    proxy: {
      '/iot/api': {
        target: 'http://192.168.101.109:9999',
        changeOrigin: true
      },
      '/iot/ws': {
        target: 'http://192.168.101.109:9999',
        changeOrigin: true,
        ws: true
      }
    }
  },
  productionSourceMap: false,
  configureWebpack: {
    plugins: buildCompressPlugins(),
    optimization: {
      runtimeChunk: 'single',
      splitChunks: {
        chunks: 'all',
        maxInitialRequests: 8,
        maxAsyncRequests: 20,
        cacheGroups: {
          elementUI: {
            name: 'chunk-elementUI',
            test: /[\\/]node_modules[\\/]element-ui[\\/]/,
            priority: 30,
            chunks: 'all',
            reuseExistingChunk: true
          },
          echarts: {
            name: 'chunk-echarts',
            test: /[\\/]node_modules[\\/](echarts|zrender)[\\/]/,
            priority: 25,
            chunks: 'all',
            reuseExistingChunk: true
          },
          three: {
            name: 'chunk-three',
            test: /[\\/]node_modules[\\/]three[\\/]/,
            priority: 25,
            chunks: 'initial',
            reuseExistingChunk: true
          },
          editors: {
            name: 'chunk-editors',
            test: /[\\/]node_modules[\\/](vue-quill-editor|quill|quill-delta)[\\/]/,
            priority: 25,
            chunks: 'all'
          },
          qrcode: {
            name: 'chunk-qrcode',
            test: /[\\/]node_modules[\\/](qrcode|qrcodejs2)[\\/]/,
            priority: 25,
            chunks: 'all'
          },
          vendors: {
            name: 'chunk-vendors',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial'
          }
        }
      }
    }
  }
}
