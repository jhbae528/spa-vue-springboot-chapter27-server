package org.hdcd.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="item")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long itemId;
	
	@Column(length = 50)
	private String itemName;
	
	private Integer price;
	
	@Column(length = 250)
	private String description;
	
	@Transient
	private MultipartFile picture;
	
	@Column(length = 200)
	private String pictureUrl;
}
