# 用户管理与 RBAC 完善设计（审阅稿 v1）

## 1. 背景与目标

用户提出用户管理 4 个不合逻辑之处，要求先出计划文档审阅再动手：

1. **操作列图标看不出作用** —— 纯图标无提示。
2. **角色编辑不从用户列表进就看不到内容** —— 侧边栏直接点是空白页。
3. **角色编辑显示所有角色、"没有权限控制"** —— 权限体系半真半假。
4. **没有新建角色功能** —— 无法创建角色并分配其能做什么。

目标：四点全部修成"能用且逻辑自洽"的 RBAC：操作有文字语义、角色编辑单入口、权限数据真实且后端有校验、角色可新建并勾选权限。

## 2. 现状与根因（均已实测，标注 文件:行号）

### 问题1：操作列图标无语义
`blog-vue3-back/src/views/system/user/index.vue:130-155`，4 个纯图标 `button-table`，无 tooltip 无文字：
- `type=edit` 编辑、`type=delete` 删除
- `icon=&#xe889; type=add` → 重置密码（handleResetPwd）
- `icon=&#xe715; type=add` → 分配角色（handleAuthRole）

后两个还错用了 `type="add"`（新增样式）。同页顶部工具栏"新增/导入"按钮因权限缺失实际是**被 v-auth 隐藏的**（见问题3数据缺口）。

### 问题2：角色编辑入口裸露
- `asyncRoutes.ts:41-67`：用户管理菜单挂了两个子项，`role/authRole/:userId(\d+)?` 正则**可选**且在菜单可见。
- `authRole.vue:114-133`：`if (userId) {...}` —— 无 userId 时 `loading` 永不置 false，基本信息空、角色表不加载 → 侧边栏直点 = 空白/转圈。

### 问题3：权限"半真半假"（三层拆开看）
| 层 | 现状 | 证据 |
|---|---|---|
| 权限数据 | **真实但残缺**：`permissions` 仅 9 条、全给角色 ADMIN(2)，USER(1) 为 0；而代码里 v-auth 字符串共 **56 个** → 缺的权限对应的按钮对所有人隐藏（如 `system:user:add` 新增用户按钮） | 远程库实测：perm_cnt=9, rp_cnt=9；`grep -rho v-auth` 去重 56 条 |
| 前端装配 | **真实**：登录响应 `permissions = selectPermissionsByRoleIds(roleIds)` → store → v-auth 按其显隐 | `UserServiceImpl:190-193`、`login/index.vue:253-259`、`directives/permission.ts` |
| 后端校验 | **零**：`SecurityConfig:37-40` 只有 `anyRequest().authenticated()`，任何登录用户可调任何管理接口 | SecurityConfig 源码 |
| 其他洞 | ① `AuthStubController GET /api/getInfo` 硬编码返回 `permissions=["*:*:*"]`（当前无调用方，但属地雷）② `authRole` 角色表返回**全部角色**可任意勾选（含 ADMIN），"权限"列显示原始标签串不友好 | `AuthStubController:11-17`、`PermissionServiceImpl:104-145` |
| 角色硬编码 | `updateUserRole` 的 validRoles 写死 `[USER, ADMIN]` 枚举（`UserServiceImpl:272`），**将来新建的角色在分配角色页选不到**；且角色不存在时 `RoleTypeEnum.valueOf` 会直接抛异常 | `UserServiceImpl:272-300` |

### 问题4：角色管理是有壳无芯
- **孤儿页面已存在**：`views/system/role/index.vue`（595 行 RuoYi 风格：新增/编辑/**菜单权限树**/数据权限/分配用户）+ `components/AllocationUser.vue` + `api/system/roleApi.ts` —— `asyncRoutes.ts` 无入口，从未接线。
- **后端全假**：`SystemRoleStubController`（`/api/system/role/**`）list 返回空、增删改返回"操作成功"不落库。
- **表结构对不上**：页面用 `roleKey/status/roleSort/remark/dataScope`，`roles` 表只有 `id/role_name/role_desc/created_at/updated_at`。
- **权限树数据源也是假**：对话框树取 `/system/menu/treeselect` → `SystemMenuStubController` → 空树。
- **可复用的真实件**：`permissions` 表（name/permission/type[menu|api|button]/parent_id/path 树形）、`role_permissions`（两端 CASCADE）、`PermissionMapper`（含 `selectPermissionsByRoleIds`）、`PermissionService.assignPermissionForRole/addNewPermission/getAuthRole`、`AdminPermissionController(/api/admin/permission/...)`、`RoleMapper.insertRole`。

## 3. 方案

### 3.1 问题1：操作列图标加语义（纯前端，独立可交付）
- 4 个按钮外包 `el-tooltip`（content=编辑/删除/重置密码/分配角色，placement top），加 `aria-label`。
- 后两个 `type="add"` 改为语义化 type（或统一 `text` 风格：图标+小字）。
- 对比方案：直接改文字按钮（占用宽，"操作"列会胖）→ 选 tooltip；全站其他页同款图标问题不在本次范围。

### 3.2 问题2：角色编辑收敛为单入口
- `asyncRoutes.ts` 角色编辑子项加 `isHide: true`（菜单不再显示，路由保留供列表跳转）。
- `authRole.vue` 无 userId 时 `ElMessage.warning` + `router.replace('/system/user-auth/role/index')` 兜底（防直接输 URL）。

### 3.3 问题3：权限真实化（数据 → 前端 → 后端三层）
**层1 权限种子补全（SQL）**
- 以代码中 56 个 v-auth 字符串为准生成 `permissions` 种子（按 模块 → 操作 两级，type=menu/button，path 预留），一次插入。
- 授予策略：角色 ADMIN 授全部；角色 USER 授 0（维持现状，纯前台注册用户无后台操作）。
- 修数据后，被隐藏的"新增用户/导入"等按钮恢复。

**层2 前端修正**
- `AuthStubController`：删除硬编码 `*:*:*` —— getInfo 改为按当前 token 查真实 roles/permissions（或直接删除该端点，因为当前无调用方，登录已带 permissions）。
- `authRole.vue`："权限"列改标签组（el-tag list）显示；**可分配角色范围**：仅显示非内置锁定角色（见下）。
- `updateUserRole` validRoles 改为 `roleMapper.getAllRoles()`（去掉枚举硬编码，新角色即可分配），并修正 `UserServiceImpl:296` 成功却打 `logger.error("角色创建失败")` 的日志反了的小 bug。

**层3 后端强制校验（范围待确认，见第9节）**
- 方案A（推荐一期）：`SecurityConfig` 加 method security + 自定义 `PermissionEvaluator`（`hasPermission(authentication, target, action)` 查 `selectPermissionsByRoleIds`），对 **用户/角色/权限管理** 三组敏感接口 `@PreAuthorize`；其余模块二期铺开。
- 方案B：URL→权限 映射表 + `HandlerInterceptor` 全量拦截（改动面大，一期不动）。
- 兜底：`AuthStubController` 地雷同时排掉。

### 3.4 问题4：角色管理转正
**入口**：asyncRoutes 用户管理下新增子项"角色管理"（`role/manage` → 改造后的 `views/system/role/index.vue`，原"角色编辑"菜单项被 3.2 隐藏）。

**页面改造（对比后推荐 B）**
- 方案A：`roles` 表加 `role_key/status/sort/remark` 列全对齐 RuoYi 页面 —— 本站无状态开关语义、无排序需求、无数据权限，加列是造伪需求。
- **方案B（推荐）**：页面简化对齐现有表 —— 保留：角色名称、描述、**菜单权限树（permissions 树）**、操作（编辑/删除/权限）；**砍掉**：数据权限弹窗、dept 树（本站无部门）、roleKey/roleSort/status 字段、`AllocationUser` 分配用户（与用户列表"分配角色"功能重复，入口留一个）。

**后端**
- 删除 `SystemRoleStubController`，新建真控制器保持**同 URL** `/api/system/role`（前端 roleApi.ts 零改动，仅删 dataScope/deptTree/export 等砍掉功能的调用）：
  - `GET /list` 分页列表（含权限数）、`GET /{roleId}` 详情+已勾选权限ids、`POST` 新增、`PUT` 修改、`DELETE /{roleId}` 删除（守卫：ADMIN 角色与当前登录人角色不可删）、`PUT /{roleId}/permissions` 批量保存勾选（replace 语义）
- `PermissionMapper` 补两方法：权限树查询 `selectPermissionTree()`（按 parent_id 建树）、`replaceRolePermissions(roleId, ids)`。
- 权限（permissions 表）自身的增删改 UI：**一期不做**（种子 SQL 维护 + 对话框只读勾选），见第9节待确认。

**角色保护规则**：ADMIN 角色不可删除、不可在权限树里被"取消全部权限"（防止把自己锁死）；非 ADMIN 账号不得授予/保留 ADMIN 角色（层3校验一并覆盖）。

## 4. 改动清单

| # | 文件 | 改动 | 所属 |
|---|---|---|---|
| 1 | `views/system/user/index.vue` | 操作列 tooltip + type 修正 | 问题1 |
| 2 | `router/modules/asyncRoutes.ts` | 角色编辑 isHide；新增"角色管理"子项 | 问题2/4 |
| 3 | `views/system/user/authRole.vue` | 无 userId 重定向；权限列改 tag；角色范围过滤 | 问题2/3 |
| 4 | `views/system/role/index.vue` | 砍数据权限/dept/roleKey 等，接真接口 | 问题4 |
| 5 | `api/system/roleApi.ts` | 删砍掉功能的方法 | 问题4 |
| 6 | 后端 `SystemRoleStubController` → `SystemRoleController` | 真 CRUD + 权限保存 | 问题4 |
| 7 | `PermissionMapper(.xml)` | selectPermissionTree / replaceRolePermissions | 问题3/4 |
| 8 | `UserServiceImpl` | validRoles 查库；日志修正 | 问题3 |
| 9 | `AuthStubController` | 删/改真 getInfo | 问题3 |
| 10 | `SecurityConfig` + PermissionEvaluator + 敏感接口 @PreAuthorize | 后端强制校验 | 问题3 |
| 11 | `database` 种子 SQL（追加 blog.sql + 线上执行） | 56 权限种子 + ADMIN 全授 | 问题3 |
| 12 | `docs/` | 实施时同步本设计稿状态 | — |

## 5. 里程碑

- **M1 纯前端体验**（问题1+2，无依赖）：tooltip、入口收敛、authRole 兜底 → 构建部署验证。
- **M2 角色管理**（问题4）：种子 SQL → 后端控制器/Mapper → 页面接线 → 角色增删改+权限勾选全流程实测。
- **M3 权限闭环**（问题3）：v-auth 种子补全 → AuthStub 排雷 → validRoles 查库 → 后端 @PreAuthorize → 用低权限账号实测接口被拒。

## 6. 验收清单

- [ ] 操作列 4 按钮 hover 均有文字提示，图标样式语义正确。
- [ ] 侧边栏无"角色编辑"；直接输 authRole URL 被弹回列表；列表进入正常。
- [ ] `system:user:add` 等按钮按角色显隐正确（ADMIN 可见新增用户，USER 登录后台按钮几乎全无）。
- [ ] 新建角色（名称+描述+勾选权限）落库，用户列表"分配角色"能选到新角色并生效。
- [ ] 删除非 ADMIN 角色 → 关联 user_roles 清理正确；ADMIN 角色删除被拒并提示。
- [ ] 非授权账号调 `PUT /api/system/role` 等敏感接口 → 403 友好提示（非"发生未知错误"）。
- [ ] `mvn clean package`、`vue-tsc + vite build` 通过；本地/线上全流程实测；未涉及页面无回归。

## 7. 里程碑之外的边界（明确不做）

- 其余 15 个 stub 控制器（Monitor/Dept/Post/Dict/Notice/Config/ToolGen 等 RuoYi 遗留页）本次不转正。
- 前台（front）用户体系、评论/浏览等前台权限不动。
- 数据权限（dept）概念不引入。

## 8. 风险与回滚

- 权限种子是**增量 INSERT**（permission 唯一键），不删现有 9 条 → 回滚=删新增行。
- 后端 @PreAuthorize 加严可能导致**现有管理接口对 USER 角色 403** —— 线上目前实际只有 ADMIN 在用后台，风险低；逐一接口灰度加注解，出问题删注解即回滚。
- 角色管理 URL 保持 `/api/system/role` 不变，前端老页面（若被深链）不会 404。

## 9. 待你确认的问题

1. **后端强制校验范围**：一期只锁"用户/角色/权限管理"三组敏感接口（推荐），还是直接全量 56 个权限对应接口都上？
2. **roles 表加不加列**：推荐方案B不加（页面对齐现有表）；若你想要角色"状态开关/排序"，选A。
3. **权限自身的增删改 UI**：一期 SQL 种子维护（推荐）还是把"菜单管理"页也转正成权限管理页？
4. **USER 角色权限**：维持 0 个（推荐，纯前台用户），还是给浏览类？
5. **AllocationUser（角色→分配用户）砍不砍**：推荐砍，统一从用户列表进（与问题2入口收敛同逻辑）。

## 10. 实施状态（2026-10-09，已上线）

§9 五个问题全部按推荐默认执行，代码已提交并部署验证：

- **M1**：操作列 4 按钮包 el-tooltip（编辑用户/删除用户/重置密码/分配角色，后两个 type 由 add 改 more）；角色编辑菜单 isHide + **路由守卫层**拦截直连（toast + 强制回列表）；authRole 权限列 tag 化（前 4 + `+N`）、行内 checkbox 预勾选（修好原 toggleRowSelection 不生效导致进页不显示已有角色的暗 bug）。
- **M2**：`SystemRoleController`（/api/system/role 的 list/get/add/update/delete/permissionTree，删除假的 SystemRoleStubController）+ `RoleService`（ADMIN 不可删/不可改名/不可清空权限）+ `role/index.vue` 重写对齐现有表（砍数据权限/部门树/roleKey，砍孤儿 AllocationUser.vue）+ 菜单"角色管理"（RoutesAlias.RoleIndex）。
- **M3**：81 条权限种子（20 模块 + 61 操作，含新增 `system:role:*`/`system:user:list`）已追加 `blog.sql` 并线上执行，ADMIN 全授、0 孤儿；`@EnableMethodSecurity` + `@Component("perm")` 校验器 + 敏感接口 `@PreAuthorize`；403 统一返回"没有权限执行此操作"；`updateUserRole` 有效角色改查库（新建角色可分配）+ 授 ADMIN 守卫 + 事务；删除 AuthStub 硬编码 `*:*:*` 的 getInfo。

**验证证据**：`mvn clean package` / `vue-tsc + vite build` 通过；API E2E 20/20（角色 CRUD/重名拒绝/ADMIN 三重保护/权限树分层/ops 与 victim 403 边界/新角色分配/非管理员授 ADMIN 拒绝/临时数据清理）；线上 CDP 实测菜单收敛、4 个 tooltip、守卫重定向、分配角色页预勾选 + `+77` 权限标签、角色创建落库。

**部署注记**：期间 Docker registry mirror（docker.1panel.live）故障且 docker.io 不通，改用「clean 构建产物 + docker cp 注入旧镜像 + docker commit」出镜像（`blog_backend:prev`/`blog_admin:prev` 留有回滚）；mirror 恢复后回归正常 `docker build` 流程。

**残留**：① UI 层"删除角色"按钮流程未逐步实测（API 层删除已覆盖）；② UI 测试数据 `hermes_ui_admin` 用户与 `UI_TEST_ROLE` 角色**仍在库中未清理**（清理语句被安全拦截，待人工执行：先删 `user_roles` 中该用户行，再删 `users` 行，最后 `DELETE FROM roles WHERE role_name='UI_TEST_ROLE'`，role_permissions 会级联）。
