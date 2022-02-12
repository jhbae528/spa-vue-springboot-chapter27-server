package org.hdcd.service;

import java.util.List;

import org.hdcd.domain.Item;

public interface ItemService {

	public List<Item> list() throws Exception;
	
	public void regist(Item item) throws Exception;
	
	public Item read(Long itemId) throws Exception;
	
	public void modify(Item item) throws Exception;
	
	public void remove(Long itemId) throws Exception;
	
	public String getPicture(Long itemId) throws Exception;
}
