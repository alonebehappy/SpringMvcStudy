package com.alibaba.model;

import java.io.Serializable;

public class ScheduleJob implements Serializable {
	private Long id;

	private String name;

	private String group;

	private String classPath;

	private String param;

	private String description;

	private String cronExp;

	private Integer status;

	private Boolean isActive;

	private String startTime;

	private String endTime;

	private String createdBy;

	private String createDate;

	private String updatedBy;

	private String updateDate;

	private String logMsg;

	private static final long serialVersionUID = 1L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group == null ? null : group.trim();
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath == null ? null : classPath.trim();
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param == null ? null : param.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public String getCronExp() {
		return cronExp;
	}

	public void setCronExp(String cronExp) {
		this.cronExp = cronExp == null ? null : cronExp.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime == null ? null : startTime.trim();
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime == null ? null : endTime.trim();
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy == null ? null : createdBy.trim();
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate == null ? null : createDate.trim();
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy == null ? null : updatedBy.trim();
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate == null ? null : updateDate.trim();
	}

	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg == null ? null : logMsg.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", id=").append(id);
		sb.append(", name=").append(name);
		sb.append(", group=").append(group);
		sb.append(", classPath=").append(classPath);
		sb.append(", param=").append(param);
		sb.append(", description=").append(description);
		sb.append(", cronExp=").append(cronExp);
		sb.append(", status=").append(status);
		sb.append(", isActive=").append(isActive);
		sb.append(", startTime=").append(startTime);
		sb.append(", endTime=").append(endTime);
		sb.append(", createdBy=").append(createdBy);
		sb.append(", createDate=").append(createDate);
		sb.append(", updatedBy=").append(updatedBy);
		sb.append(", updateDate=").append(updateDate);
		sb.append(", logMsg=").append(logMsg);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}
