<template>
  <div class="shop-detail-owner" v-loading="loading">
    <el-table v-if="owner" :data="[owner]" border stripe>
      <el-table-column label="头像" width="100" align="center">
        <template slot-scope="{ row }">
          <el-image
            style="width: 60px; height: 60px; border-radius: 4px;"
            :src="row.url || '/img/noPhoto.jpg'"
            :preview-src-list="row.url ? [row.url] : []"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="姓名" min-width="150">
        <template slot-scope="{ row }">{{ row.name }}({{ row.link }})</template>
      </el-table-column>
      <el-table-column label="性别" width="70" align="center">
        <template slot-scope="{ row }">{{ row.sex === '0' ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="idCard" label="身份证" min-width="170">
        <template slot-scope="{ row }">{{ row.idCard || '-' }}</template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="160">
        <template slot-scope="{ row }">{{ row.address || '-' }}</template>
      </el-table-column>
      <el-table-column prop="roomCount" label="房屋数" width="80" align="center">
        <template slot-scope="{ row }">{{ row.roomCount || 0 }}</template>
      </el-table-column>
      <el-table-column prop="memberCount" label="成员数" width="80" align="center">
        <template slot-scope="{ row }">{{ row.memberCount || 0 }}</template>
      </el-table-column>
      <el-table-column prop="carCount" label="车辆数" width="80" align="center">
        <template slot-scope="{ row }">{{ row.carCount || 0 }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无业主信息" />
  </div>
</template>

<script>
import { queryOwners } from '@/api/owner/ownerApi'

export default {
  name: 'ShopDetailOwner',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      owner: null
    }
  },
  mounted() {
    this.loadOwner()
  },
  watch: {
    ownerId: {
      handler() { this.loadOwner() }
    }
  },
  methods: {
    async loadOwner() {
      this.owner = null
      if (!this.ownerId) return
      this.loading = true
      try {
        const res = await queryOwners({
          ownerId: this.ownerId,
          ownerTypeCd: '1001',
          page: 1,
          row: 1
        })
        const list = res.data || []
        this.owner = list.length ? list[0] : null
        if (this.owner) {
          this.owner.memberCount = 0
          const memberRes = await queryOwners({
            ownerTypeCd: '2002',
            ownerId: this.ownerId,
            page: 1,
            row: 100
          })
          const members = memberRes.data || []
          this.owner.memberCount = members.length || 0
        }
      } catch (e) {
        this.owner = null
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
