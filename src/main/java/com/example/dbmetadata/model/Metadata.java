package com.example.dbmetadata.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Metadata {
	
	private String name;
	private String type;
	private String owner;
	private LocalDateTime createdTimestamp;
	private LocalDateTime updatedTimestamp;
	private String parent;

}
