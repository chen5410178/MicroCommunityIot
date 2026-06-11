<template>
  <div class="car-detail">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>车辆信息</span>
        <el-button size="small" @click="$router.go(-1)">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><label>车牌号：</label><span>{{ carInfo.carNum }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>车品牌：</label><span>{{ carInfo.carBrand || '-' }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>车类型：</label><span>{{ carInfo.leaseTypeName }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>颜色：</label><span>{{ carInfo.carColor || '-' }}</span></div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><label>车牌类型：</label><span>{{ carInfo.carTypeName }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>起租时间：</label><span>{{ carInfo.startTime }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>结租时间：</label><span>{{ carInfo.endTime }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>备注：</label><span>{{ carInfo.remark }}</span></div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><label>车位：</label><span>{{ carInfo.paNum }}-{{ carInfo.num }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>状态：</label><span>{{ carInfo.stateName }}</span></div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><label>关联房屋：</label><span>{{ carInfo.roomName }}</span></div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="成员车辆" name="carDetailMember">
          <div v-loading="tabLoading">
            <el-table :data="memberCars" border stripe>
              <el-table-column prop="carNum" label="车牌号" min-width="120" />
              <el-table-column prop="roomName" label="房屋号" min-width="120" />
              <el-table-column prop="carBrand" label="车辆品牌" min-width="100" />
              <el-table-column prop="carTypeName" label="车辆类型" min-width="100" />
              <el-table-column prop="carColor" label="颜色" width="80" />
              <el-table-column label="业主" min-width="120">
                <template slot-scope="{ row }">{{ row.ownerName }}({{ row.link }})</template>
              </el-table-column>
              <el-table-column label="车位" min-width="130">
                <template slot-scope="{ row }">
                  <template v-if="row.areaNum">{{ row.areaNum }}车场{{ row.num }}车位</template>
                  <template v-else>无</template>
                </template>
              </el-table-column>
              <el-table-column prop="startTime" label="起租时间" min-width="150" />
              <el-table-column prop="endTime" label="截止时间" min-width="150" />
              <el-table-column prop="stateName" label="状态" min-width="100" />
            </el-table>
          </div>
        </el-tab-pane>
        <el-tab-pane label="业主信息" name="carDetailOwner">
          <div v-loading="tabLoading">
            <el-table :data="ownerInfo" border stripe v-if="ownerInfo.length">
              <el-table-column prop="name" label="业主名称" min-width="120" />
              <el-table-column label="性别" width="60">
                <template slot-scope="{ row }">{{ row.sex === 0 ? '男' : '女' }}</template>
              </el-table-column>
              <el-table-column prop="idCard" label="身份证" min-width="180" />
              <el-table-column prop="link" label="联系方式" min-width="120" />
              <el-table-column prop="address" label="家庭住址" min-width="200" />
              <el-table-column prop="remark" label="备注" min-width="150" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="业主房屋" name="ownerDetailRoom">
          <div v-loading="tabLoading">
            <el-table :data="ownerRooms" border stripe>
              <el-table-column prop="roomName" label="房屋号" min-width="150" />
              <el-table-column prop="floorName" label="楼栋" min-width="120" />
              <el-table-column prop="unitName" label="单元" min-width="120" />
            </el-table>
            <el-empty v-if="!tabLoading && ownerRooms.length === 0" description="暂无数据" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="业主车辆" name="ownerDetailCar">
          <div v-loading="tabLoading">
            <el-table :data="ownerCars" border stripe>
              <el-table-column prop="carNum" label="车牌号" min-width="120" />
              <el-table-column prop="roomName" label="房屋号" min-width="120" />
              <el-table-column prop="carTypeName" label="车辆类型" min-width="100" />
              <el-table-column label="车位" min-width="130">
                <template slot-scope="{ row }">
                  <template v-if="row.areaNum">{{ row.areaNum }}车场{{ row.num }}车位</template>
                  <template v-else>无</template>
                </template>
              </el-table-column>
              <el-table-column prop="startTime" label="起租时间" min-width="150" />
              <el-table-column prop="endTime" label="截止时间" min-width="150" />
              <el-table-column prop="stateName" label="状态" min-width="100" />
            </el-table>
          </div>
        </el-tab-pane>
        <el-tab-pane label="出入场记录" name="carDetailCarInout">
          <div v-loading="tabLoading">
            <el-table :data="carInoutList" border stripe>
              <el-table-column prop="inoutId" label="进出场编号" min-width="120" />
              <el-table-column label="车辆状态" min-width="120">
                <template slot-scope="{ row }">{{ row.carInout === '3306' ? '进场' : '出场' }}({{ row.stateName }})</template>
              </el-table-column>
              <el-table-column prop="carNum" label="车牌号" min-width="120" />
              <el-table-column prop="paNum" label="停车场" min-width="120" />
              <el-table-column prop="inTime" label="进场时间" min-width="150" />
              <el-table-column label="出场时间" min-width="150">
                <template slot-scope="{ row }">{{ row.carInout !== '3307' ? '-' : row.openTime }}</template>
              </el-table-column>
            </el-table>
            <el-pagination
              :current-page="inoutPage.page"
              :page-size="inoutPage.row"
              :total="inoutPage.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="e => { inoutPage.row = e; loadCarInout() }"
              @current-change="e => { inoutPage.page = e; loadCarInout() }"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="月卡订单" name="carDetailMonthOrder">
          <el-empty description="月卡订单功能待开发" />
        </el-tab-pane>
        <el-tab-pane label="优惠券" name="carDetailCoupon">
          <el-empty description="优惠券功能待开发" />
        </el-tab-pane>
        <el-tab-pane label="优惠券核销" name="carDetailCouponOrder">
          <el-empty description="优惠券核销功能待开发" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { queryOwnerCars, queryOwnerCarsByOwner, queryOwners, listAdminCarInoutDetails } from '@/api/car/carApi'
import { queryRooms } from '@/api/property/propertyApi'

export default {
  name: 'CarDetail',
  data() {
    return {
      activeTab: 'carDetailMember',
      tabLoading: false,
      memberId: '',
      carInfo: {},
      memberCars: [],
      ownerInfo: [],
      ownerRooms: [],
      ownerCars: [],
      carInoutList: [],
      inoutPage: { page: 1, row: 10, total: 0 }
    }
  },
  mounted() {
    this.memberId = this.$route.query.memberId || ''
    if (this.memberId) {
      this.loadCarDetail()
    }
  },
  methods: {
    async loadCarDetail() {
      try {
        const res = await queryOwnerCars({
          page: 1, row: 1,
          memberId: this.memberId,
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        const data = res.data
        if (data && data.length > 0) {
          this.carInfo = data[0]
        }
      } catch { /* ignore */ }
    },
    handleTabClick(tab) {
      const tabName = tab.name
      if (tabName === 'carDetailMember') this.loadMemberCars()
      else if (tabName === 'carDetailOwner') this.loadOwnerInfo()
      else if (tabName === 'ownerDetailRoom') this.loadOwnerRooms()
      else if (tabName === 'ownerDetailCar') this.loadOwnerCars()
      else if (tabName === 'carDetailCarInout') this.loadCarInout()
    },
    async loadMemberCars() {
      this.tabLoading = true
      try {
        const res = await queryOwnerCarsByOwner({
          page: 1, row: 100,
          carId: this.carInfo.carId,
          carTypeCd: '1002',
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        this.memberCars = res.data || []
      } catch { /* ignore */ } finally {
        this.tabLoading = false
      }
    },
    async loadOwnerInfo() {
      this.tabLoading = true
      try {
        if (!this.carInfo.ownerId) { this.ownerInfo = []; return }
        const res = await queryOwners({
          page: 1, row: 100,
          memberId: this.carInfo.ownerId,
          ownerTypeCd: '1001',
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        this.ownerInfo = res.owners || res.data || []
      } catch { /* ignore */ } finally {
        this.tabLoading = false
      }
    },
    async loadOwnerRooms() {
      this.tabLoading = true
      try {
        if (!this.carInfo.ownerId) { this.ownerRooms = []; return }
        const res = await queryRooms({
          page: 1, row: 100,
          ownerId: this.carInfo.ownerId,
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        this.ownerRooms = res.data || []
      } catch { /* ignore */ } finally {
        this.tabLoading = false
      }
    },
    async loadOwnerCars() {
      this.tabLoading = true
      try {
        if (!this.carInfo.ownerId) { this.ownerCars = []; return }
        const res = await queryOwnerCarsByOwner({
          page: 1, row: 100,
          ownerId: this.carInfo.ownerId,
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        this.ownerCars = res.data || []
      } catch { /* ignore */ } finally {
        this.tabLoading = false
      }
    },
    async loadCarInout() {
      this.tabLoading = true
      try {
        const res = await listAdminCarInoutDetails({
          ...this.inoutPage,
          carNum: this.carInfo.carNum,
          communityId: this.$store.state.app.currentCommunity.communityId || ''
        })
        this.carInoutList = res.data || []
        this.inoutPage.total = res.records || 0
      } catch { /* ignore */ } finally {
        this.tabLoading = false
      }
    }
  }
}
</script>

<style scoped>
.info-item { padding: 8px 0; font-size: 14px; }
.info-item label { color: #909399; }
.info-item span { color: #303133; margin-left: 4px; }
.tab-card { margin-top: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
