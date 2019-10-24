/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2019.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.informix.springboot;

import org.bson.BasicBSONObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class SimpleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Type(type = "com.informix.springboot.HibernateBasicBSONObject")
	private BasicBSONObject detail;

	protected SimpleEntity() {
	}

	public SimpleEntity(String name, BasicBSONObject detail) {
		this.name = name;
		this.detail = detail;
	}

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
		this.name = name;
	}

	public BasicBSONObject getDetail() {
		return detail;
	}

	public void setDetail(BasicBSONObject detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return String.format("SimpleEntity[id=%d, name='%s', detail='%s']", id, name, detail.toString());
	}
}
