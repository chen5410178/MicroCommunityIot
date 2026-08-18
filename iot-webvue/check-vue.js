const fs = require('fs')
const parser = require('@babel/parser')

const files = [
  'src/components/shop/ShopLocationTree.vue',
  'src/views/shop/listShopManage.vue',
  'src/views/shop/shopDetail.vue',
  'src/views/shop/tabs/ShopDetailOwner.vue',
  'src/views/shop/tabs/ShopDetailRoom.vue',
  'src/views/shop/tabs/ShopDetailMember.vue',
  'src/views/shop/tabs/ShopDetailCar.vue',
  'src/views/shop/tabs/ShopDetailShop.vue'
]

let failed = false
for (const f of files) {
  const content = fs.readFileSync(f, 'utf8')
  const sm = content.match(/<script>([\s\S]*?)<\/script>/)
  if (sm) {
    try {
      parser.parse(sm[1], { sourceType: 'module', plugins: ['classProperties'] })
      console.log('SCRIPT OK:', f)
    } catch (e) {
      failed = true
      console.log('SCRIPT ERROR:', f, e.message)
    }
  }
}
process.exit(failed ? 1 : 0)
