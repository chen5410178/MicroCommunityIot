module.exports = {
  devServer: {
    port: 3002,
    proxy: {
      '/iot/api': {
        target: 'http://127.0.0.1:9999',
        changeOrigin: true
      },
      '/ws': {
        target: 'http://127.0.0.1:9999',
        changeOrigin: true,
        ws: true
      }
    }
  }
}
