package com.company.common.base.persist.s3;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectListing;
//import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 */
public class S3Helper {
    /*
    public static List<S3ObjectSummary> fullListObjects(AmazonS3 s3, String bucketName, String prefix) {
        ObjectListing objListing = s3.listObjects(bucketName, prefix);

        List<S3ObjectSummary> result = objListing.getObjectSummaries();

        while (objListing.isTruncated()) {
            objListing = s3.listNextBatchOfObjects(objListing);
            result.addAll(objListing.getObjectSummaries());
        }
        return result;
    }
    */
}
