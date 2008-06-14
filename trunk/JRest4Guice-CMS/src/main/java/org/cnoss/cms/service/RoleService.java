/*
 * @(#)RoleService.java   08/06/13
 * 
 * Copyright (c) 2008 conss 开发团队 
 *
 * @author 胡晓豪
 *
 *
 */



package org.cnoss.cms.service;

//~--- non-JDK imports --------------------------------------------------------

import org.cnoss.cms.entity.Role;
import org.cnoss.cms.exception.ServiceException;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * 角色模块业务处理接口
 *
 *
 */
public interface RoleService {

    /**
     * 创建角色信息
     *
     * @param role 角色
     */
    public void createRole(Role role) throws ServiceException;

    /**
     * 修改角色信息
     *
     * @param role 角色
     */
    public void editRole(Role role) throws ServiceException;

    /**
     * 删除角色信息
     *
     * @param roleId 角色编号
     * @throws ServiceException
     */
    public void deleteRole(Long roleId) throws ServiceException;

    /**
     * 取得角色
     *
     * @param roleId 角色编号
     * @return role 编号对应的角色信息
     * @throws ServiceException
     */
    public Role getRole(Long roleId) throws ServiceException;

    /**
     * 列表角色
     * @return 角色列表
     * @throws ServiceException
     */
    public List<Role> getRoles() throws ServiceException;
}