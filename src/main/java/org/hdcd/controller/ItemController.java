package org.hdcd.controller;

import java.io.File;
import java.util.List;

import org.hdcd.domain.Item;
import org.hdcd.service.ItemService;
import org.hdcd.util.MediaUtils;
import org.hdcd.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/items")
public class ItemController {
	
	private final ItemService itemService;
	
	@Value("${upload.path}")
	private String uploadPath;
	
	@PostMapping
	public ResponseEntity<Item> register(@Validated @RequestBody Item item) throws Exception {
		
		String[] files = item.getFiles();
		
		for (int i = 0; i < files.length; i++) {
			log.info("files[i] = " + files[i]);
		}
		
		this.itemService.regist(item);
		
		log.info("register item.getItemId = " + item.getItemId());
		
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Item>> list() throws Exception {
		log.info("list");
		List<Item> itemList = this.itemService.list();
		
		return new ResponseEntity<List<Item>>(itemList, HttpStatus.OK);				
	}
	
	@GetMapping("/{itemId}")
	public ResponseEntity<Item> read(@PathVariable("itemId") Long itemId) throws Exception {
		log.info("read");
		
		Item item = this.itemService.read(itemId);
		
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Item> modify(@Validated @RequestBody Item item) throws Exception {
		
		String[] files = item.getFiles();
		
		for (int i = 0; i < files.length; i++) {
			log.info("files[i] = " + files[i]);
		}				
		
		this.itemService.modify(item);
		
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}
	
	@DeleteMapping("/{itemId}")
	public ResponseEntity<Void> remove(@PathVariable("itemId") Long itemId) throws Exception {
		log.info("remove");
		
		this.itemService.remove(itemId);
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/upload", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> upload(MultipartFile file) throws Exception {
		
		log.info("originalName: " + file.getOriginalFilename());
		
		String savedName = UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes());
		
		return new ResponseEntity<String>(savedName, HttpStatus.CREATED);
	}
	
	@GetMapping("/display")	
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception {
		
		ResponseEntity<byte[]> entity = null;
		
		log.info("FILE NAME: " + fileName);
		
		try {
			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
			
			MediaType mediaType = MediaUtils.getMediaType(formatName);
			
			HttpHeaders headers = new HttpHeaders();
			
			File file = new File(uploadPath + fileName);
			
			if (mediaType != null) {
				headers.setContentType(mediaType);
			}
			else {
				fileName = fileName.substring(fileName.indexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
			}
			
			entity = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.CREATED);
		}
		catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}

	@GetMapping("/attach/{itemId}")
	public List<String> attach(@PathVariable("itemId") Long itemId) throws Exception {
		log.info("attach itemId: " + itemId);
		return itemService.getAttach(itemId);
	}
}
