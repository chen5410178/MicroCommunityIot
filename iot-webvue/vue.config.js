module.exports = {
  devServer: {
    port: 3002,
    client: {
      webSocketURL: 'auto://0.0.0.0:0/dev-ws'
    },
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
  }
}
