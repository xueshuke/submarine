/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.submarine.server.rest.workbench;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

import org.apache.submarine.server.utils.response.JsonResponse;
import org.apache.submarine.server.utils.response.JsonResponse.ListResult;
import org.apache.submarine.server.rest.workbench.annotation.SubmarineApi;
import org.apache.submarine.server.workbench.database.entity.SysUserEntity;
import org.apache.submarine.server.workbench.database.service.SysUserService;
import org.apache.submarine.server.api.workbench.Action;
import org.apache.submarine.server.api.workbench.Permission;
import org.apache.submarine.server.api.workbench.Role;
import org.apache.submarine.server.api.workbench.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sys/user")
@Produces("application/json")
@Singleton
public class SysUserRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(SysUserRestApi.class);

  private SysUserService userService = new SysUserService();
  private static final Gson gson = new Gson();

  @Inject
  public SysUserRestApi() {
  }

  @GET
  @Path("/list")
  @SubmarineApi
  public Response queryPageList(@QueryParam("userName") String userName,
                                @QueryParam("email") String email,
                                @QueryParam("deptCode") String deptCode,
                                @QueryParam("column") String column,
                                @QueryParam("field") String field,
                                @QueryParam("pageNo") int pageNo,
                                @QueryParam("pageSize") int pageSize) {
    LOG.info("queryDictList userName:{}, email:{}, deptCode:{}, " +
            "column:{}, field:{}, pageNo:{}, pageSize:{}",
        userName, email, deptCode, column, field, pageNo, pageSize);

    List<SysUserEntity> list = null;
    try {
      list = userService.queryPageList(userName, email, deptCode, column, field, pageNo, pageSize);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new JsonResponse.Builder<>(Response.Status.OK).success(false).build();
    }
    PageInfo<SysUserEntity> page = new PageInfo<>(list);
    ListResult<SysUserEntity> listResult = new ListResult(list, page.getTotal());

    return new JsonResponse.Builder<ListResult<SysUserEntity>>(Response.Status.OK)
        .success(true).result(listResult).build();
  }

  @PUT
  @Path("/edit")
  @SubmarineApi
  public Response edit(SysUserEntity sysUser) {
    LOG.info("edit({})", sysUser.toString());

    try {
      userService.edit(sysUser);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new JsonResponse.Builder<>(Response.Status.OK)
          .message("Update user failed!").success(false).build();
    }
    return new JsonResponse.Builder<>(Response.Status.OK)
        .success(true).message("Update user successfully!").build();
  }

  @POST
  @Path("/add")
  @SubmarineApi
  public Response add(SysUserEntity sysUser) {
    LOG.info("add({})", sysUser.toString());

    try {
      userService.add(sysUser);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new JsonResponse.Builder<>(Response.Status.OK).success(false)
          .message("Save user failed!").build();
    }

    return new JsonResponse.Builder<SysUserEntity>(Response.Status.OK)
        .success(true).message("Save user successfully!").result(sysUser).build();
  }

  @DELETE
  @Path("/delete")
  @SubmarineApi
  public Response delete(@QueryParam("id") String id) {
    LOG.info("delete({})", id);

    try {
      userService.delete(id);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new JsonResponse.Builder<>(Response.Status.OK).success(false)
          .message("delete user failed!").build();
    }
    return new JsonResponse.Builder<>(Response.Status.OK)
        .success(true).message("delete  user successfully!").build();
  }

  @PUT
  @Path("/changePassword")
  @SubmarineApi
  public Response changePassword(SysUserEntity sysUser) {
    LOG.info("changePassword({})", sysUser.toString());

    try {
      userService.changePassword(sysUser);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new JsonResponse.Builder<>(Response.Status.OK).success(false)
          .message("delete user failed!").build();
    }
    return new JsonResponse.Builder<>(Response.Status.OK)
        .success(true).message("delete  user successfully!").build();
  }

  @GET
  @Path("/info")
  @SubmarineApi
  public Response info() {
    List<Action> actions = new ArrayList<Action>();
    Action action1 = new Action("add", false, "add");
    Action action2 = new Action("query", false, "query");
    Action action3 = new Action("get", false, "get");
    Action action4 = new Action("update", false, "update");
    Action action5 = new Action("delete", false, "delete");
    actions.add(action1);
    actions.add(action2);
    actions.add(action3);
    actions.add(action4);
    actions.add(action5);

    Permission.Builder permissionBuilder1 = new Permission.Builder("admin", "dashboard", "dashboard");
    Permission permission1 = permissionBuilder1.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder2 = new Permission.Builder("admin", "exception", "exception");
    Permission permission2 = permissionBuilder2.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder3 = new Permission.Builder("admin", "result", "result");
    Permission permission3 = permissionBuilder3.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder4 = new Permission.Builder("admin", "profile", "profile");
    Permission permission4 = permissionBuilder4.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder5 = new Permission.Builder("admin", "table", "table");
    Permission permission5 = permissionBuilder5.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder6 = new Permission.Builder("admin", "form", "form");
    Permission permission6 = permissionBuilder6.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder7 = new Permission.Builder("admin", "order", "order");
    Permission permission7 = permissionBuilder7.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder8 = new Permission.Builder("admin", "permission", "permission");
    Permission permission8 = permissionBuilder8.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder9 = new Permission.Builder("admin", "role", "role");
    Permission permission9 = permissionBuilder9.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder10 = new Permission.Builder("admin", "table", "table");
    Permission permission10 = permissionBuilder10.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder11 = new Permission.Builder("admin", "user", "user");
    Permission permission11 = permissionBuilder11.actions(actions).actionEntitySet(actions).build();

    Permission.Builder permissionBuilder12 = new Permission.Builder("admin", "support", "support");
    Permission permission12 = permissionBuilder12.actions(actions).actionEntitySet(actions).build();

    List<Permission> permissions = new ArrayList<Permission>();
    permissions.add(permission1);
    permissions.add(permission2);
    permissions.add(permission3);
    permissions.add(permission4);
    permissions.add(permission5);
    permissions.add(permission6);
    permissions.add(permission7);
    permissions.add(permission8);
    permissions.add(permission9);
    permissions.add(permission10);
    permissions.add(permission11);
    permissions.add(permission12);

    Role.Builder roleBuilder = new Role.Builder("admin", "admin");
    Role role = roleBuilder.describe("Permission").status(1).creatorId("system")
        .createTime(1497160610259L).deleted(0).permissions(permissions).build();

    UserInfo.Builder userInfoBuilder = new UserInfo.Builder("4291d7da9005377ec9aec4a71ea837f", "admin");
    UserInfo userInfo = userInfoBuilder.username("admin").password("")
        .avatar("/avatar2.jpg").status(1).telephone("").lastLoginIp("27.154.74.117")
        .lastLoginTime(1534837621348L).creatorId("admin")
        .createTime(1497160610259L).merchantCode("TLif2btpzg079h15bk")
        .deleted(0).roleId("admin").role(role).build();

    return new JsonResponse.Builder<UserInfo>(Response.Status.OK).success(true).result(userInfo).build();
  }

  @POST
  @Path("/2step-code")
  @SubmarineApi
  public Response step() {
    String data = "{stepCode:1}";

    return new JsonResponse.Builder<>(Response.Status.OK).success(true).result(data).build();
  }
}
