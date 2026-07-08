module.exports = {
  devServer: {
    port: 3002,
    proxy: {
      '/iot/api': {
        target: 'http://192.168.101.109:9999',
        changeOrigin: true
      },
      '/ws': {
        target: 'http://192.168.101.109:9999',
        changeOrigin: true,
        ws: true
      }
    }
  }
}
