package com.caremoa.member.controller;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.caremoa.member.adapter.ContractCompleted;
import com.caremoa.member.domain.dto.LoginDto;
import com.caremoa.member.domain.dto.MemberDto;
import com.caremoa.member.domain.service.MemberService;
import com.caremoa.member.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* @packageName    : com.caremoa.member.controller
* @fileName       : MemberController.java
* @author         : 이병관
* @date           : 2023.05.14
* @description    :
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2023.05.14        이병관       최초 생성
*/
@Slf4j
@RestController
@Tag(name = "회원관리", description = "CareMoa 회원관리")
@RequiredArgsConstructor
public class MemberController {

	final private MemberService service;
	final private StreamBridge streamBridge;

	/**
	 * @methodName    : getAll
	 * @date          : 2023.05.31
	 * @description   : 전체 리스트를 페이지 단위로 조회한다.(GET)
	 * @param pageable
	 * @return
	*/
	@Operation(summary = "회원정보 조회" , description = "회원정보 조회" )
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the members", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content) })
	@GetMapping("/members")
	public ResponseEntity<Page<MemberDto>> getAll(Pageable pageable) {
		try {
			log.info("findAll");
			return ResponseEntity.ok().body(service.getAll(pageable).map(MemberDto::toDto));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(null);
		}
	}

	/**
	 * @methodName    : getById
	 * @date          : 2023.05.31
	 * @description   : ID로 조회한다.(GET)
	 * @param id
	 * @return
	*/
	@Operation(summary = "회원정보 Key조회" , description = "회원정보 Primary Key로 조회" )
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found the MemberDto", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)) }),
			@ApiResponse(responseCode = "404", description = "MemberDto not found", content = @Content) })
	@GetMapping("/members/{id}")
	public ResponseEntity<MemberDto> getById(@PathVariable("id") Long id) {
		try {
		    return new ResponseEntity<>(MemberDto.toDto(service.getById(id)),HttpStatus.OK);
		}catch( ApiException apiEx ) {
		    return new ResponseEntity<>(null, apiEx.getCode());
	    }catch (Exception e) {
			return ResponseEntity.internalServerError().body(null);
		}
	}


	/**
	 * @methodName    : postData
	 * @date          : 2023.05.31
	 * @description   : 데이터를 입력한다.(POST)
	 * @param newData
	 * @return
	*/
	@Operation(summary = "회원정보 등록" , description = "회원정보 신규 데이터 등록" )
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Create the MemberDto", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content) })
	@PostMapping("/members")
	ResponseEntity<MemberDto> postData(@RequestBody MemberDto newData) {
		try {
			return new ResponseEntity<>(MemberDto.toDto(service.postData(newData.toModel())), HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @methodName    : putData
	 * @date          : 2023.05.31
	 * @description   : DTO 데이터를 전체 수정한다.(PUT)
	 * @param newData
	 * @param id
	 * @return
	*/
	@Operation(summary = "회원정보 수정" , description = "회원정보 데이터 수정" )
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Update the MemberDto", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)) }),
			@ApiResponse(responseCode = "404", description = "MemberDto not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)})
	@PutMapping("/members/{id}")
	ResponseEntity<MemberDto> putData(@RequestBody MemberDto newData,
			@PathVariable("id") Long id) {
		try {
			return new ResponseEntity<>(MemberDto.toDto(service.putData(newData.toModel(),id)), HttpStatus.OK);
		}catch( ApiException apiEx ) {
		     return new ResponseEntity<>(null, apiEx.getCode());
	    } catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @methodName    : patchData
	 * @date          : 2023.05.31
	 * @description   : 전달받은 데이터만 수정한다.(Patch)
	 * @param newData
	 * @param id
	 * @return
	*/
	@Operation(summary = "회원정보 수정" , description = "회원정보 데이터 수정" )
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Update the MemberDto", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)) }),
			@ApiResponse(responseCode = "404", description = "HelperDto not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)})
	@PatchMapping("/members/{id}")
	ResponseEntity<MemberDto> patchData(@RequestBody MemberDto newData,
			@PathVariable("id") Long id) {
		try {
			return new ResponseEntity<>(MemberDto.toDto(service.putData(newData.toModel(),id)), HttpStatus.OK);
		}catch( ApiException apiEx ) {
		     return new ResponseEntity<>(null, apiEx.getCode());
	    } catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * @methodName    : deleteData
	 * @date          : 2023.05.31
	 * @description   : 데이터를 삭제한다.
	 * @param id
	 * @return
	*/
	@Operation(summary = "회원정보 삭제" , description = "회원정보 Primary Key로 삭제" )
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Delete the MemberDto", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content) })
	@DeleteMapping("/members/{id}")
	public ResponseEntity<HttpStatus> deleteData(@PathVariable("id") Long id) {
		try {
			service.deleteData(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "카프카 Publish 테스트" , description = "카프카 Publish 테스트" )
	@GetMapping("/testkafka/{memberId}/{helperId}")
	public ResponseEntity<HttpStatus> test(@PathVariable("memberId") Long memberId, @PathVariable("helperId") Long helperId) {
		try {
			ContractCompleted xx = ContractCompleted.builder().helperId(helperId).memberId(memberId).build();
			String json = xx.toJson();
			log.info("before publish");
		    if( json != null ){
		          streamBridge.send("basicProducer-out-0", MessageBuilder.withPayload(json)
		      	 		.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());
		     }
		    log.info("after publish");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			log.info("publish {}", e.getMessage());
			return ResponseEntity.internalServerError().body(null);
		}
	}
	
	/**
	 * @methodName    : Login
	 * @date          : 2023.05.31
	 * @description   : ID로 조회한다.(GET)
	 * @param id
	 * @return
	 * @throws Exception 
	 * @throws ApiException 
	*/
	@Operation(summary = "로그인" , description = "인증 로그인" )
	@GetMapping("/login")
	public LoginDto findUserId(@RequestParam("userId") String userId) throws ApiException, Exception {
		return service.findUserId(userId);
	}
}
