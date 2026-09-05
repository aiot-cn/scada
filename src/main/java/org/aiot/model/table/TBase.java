
package org.aiot.model.table;

import org.aiot.lang.annotation.AoReflect;
import org.nutz.dao.entity.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class TBase implements Serializable,Comparable{

	//private static final long serialVersionUID = 5294893360106912988L;

	@Id(auto=false)
	private Long id;

	@AoReflect("创建时间")
	private Date createDate;

	@AoReflect("创建者")
    private Long createBy;

	@AoReflect("修改时间")
    private Date updateDate;

	@AoReflect("修改者")
    private Long updateBy;     
    private int isRemoved;
    
    /**
     * 其它命名规范
     * 
    private String name;//名称
    prviate String code;//编码
    private String value;//值
    private String helpCode;//助记符
    private Integer type;//类型
    private Long parentId;//上级
    private Integer status;//状态   
    private Integer sequence;//排序
    private String remark;//备注
    */
    
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Long getCreateBy() {
		return createBy;
	}
	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}
	public Long getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public int getIsRemoved() {
		return isRemoved;
	}

	public void setIsRemoved(int isRemoved) {
		this.isRemoved = isRemoved;
	}

	@Override
	public int compareTo(Object o) {
    	TBase base = (TBase) o;
		return this.getId().compareTo(base.getId());
	}
}