package org.hdcd.service;

import java.util.ArrayList;
import java.util.List;

import org.hdcd.domain.Item;
import org.hdcd.domain.ItemFile;
import org.hdcd.repository.ItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

	private final ItemRepository repository;
	
	@Transactional
	public void regist(Item item) throws Exception {
		Item itemEntity = new Item();
		
		itemEntity.setItemName(item.getItemName());
		itemEntity.setPrice(item.getPrice());
		itemEntity.setDescription(item.getDescription());
		
		String[] files = item.getFiles();
		
		if (files == null) {
			return;
		}
		
		for (String fileName : files) {
			ItemFile itemFile = new ItemFile();
			itemFile.setFullName(fileName);
			itemEntity.addItemFile(itemFile);
		}

		repository.save(itemEntity);
	}

	@Transactional(readOnly = true)
	public List<Item> list() throws Exception {
		return repository.findAll(Sort.by(Direction.DESC, "itemId"));
	}
	
	@Transactional(readOnly = true)
	public Item read(Long itemId) throws Exception {
		return repository.getOne(itemId);
	}

	@Transactional
	public void modify(Item item) throws Exception {
		Item itemEntity = repository.getOne(item.getItemId());
		
		itemEntity.setItemName(item.getItemName());
		itemEntity.setPrice(item.getPrice());
		itemEntity.setDescription(item.getDescription());
		
		String[] files = item.getFiles();
		
		if (files != null) {
			itemEntity.clearItemFile();
			
			for (String fileName : files) {
				ItemFile itemFile = new ItemFile();
				itemFile.setFullName(fileName);
				itemEntity.addItemFile(itemFile);
			}
		}

		repository.save(itemEntity);		
	}
	
	@Transactional
	public void remove(Long itemId) throws Exception {
		repository.deleteById(itemId);		
	}

	@Override
	public List<String> getAttach(Long itemId) throws Exception {
		Item itemEntity = repository.getOne(itemId);
		
		List<ItemFile> itemFiles = itemEntity.getItemFiles();
		
		List<String> attachList = new ArrayList<String>();
		for(ItemFile itemFile : itemFiles) {
			attachList.add(itemFile.getFullName());
		}
		
		return attachList;
	}
}
