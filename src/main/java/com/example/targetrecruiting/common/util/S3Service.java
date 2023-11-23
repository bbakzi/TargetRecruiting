package com.example.targetrecruiting.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.targetrecruiting.recruitingBoard.entity.Board;
import com.example.targetrecruiting.recruitingBoard.entity.BoardImage;
import com.example.targetrecruiting.recruitingBoard.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final FileRepository fileRepository;

    private final String S3_BUCKET_PREFIX ="S3";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //파일 업로드
    public String uploadFile(MultipartFile multipartFile)throws IOException {
        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());

        amazonS3.putObject(new PutObjectRequest(bucketName,fileName,multipartFile.getInputStream(),objectMetadata).
                withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    //다중 파일 등록 팩토리
    public List<BoardImage> fileFactory(List<MultipartFile> images, Board board) throws IOException{
        List<BoardImage> boardImageList = new ArrayList<>();

        images = images.stream()
                .map(s -> s.isEmpty() ? null : s)
                .toList();

        for (MultipartFile image : images){
            if (image == null){
                boardImageList = board.getBoardImageList();
                break;
            }

            fileRepository.deleteByBoardId(board.getId());

            String fileName = UUID.randomUUID() + "_" +image.getOriginalFilename();
            String imageUrl = null;

            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(image.getContentType());
            objectMetadata.setContentLength(image.getSize());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(),objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = amazonS3.getUrl(bucketName,fileName).toString();

            boardImageList.add(new BoardImage(imageUrl, board));
        }

        return boardImageList;
    }
}
