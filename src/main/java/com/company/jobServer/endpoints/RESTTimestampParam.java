package com.company.jobServer.endpoints;

import javax.ws.rs.WebApplicationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RESTTimestampParam {

  private SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
  private java.sql.Timestamp timestamp;

  public RESTTimestampParam( String timestampStr ) throws WebApplicationException {
    try {
      timestamp = new java.sql.Timestamp( df.parse( timestampStr ).getTime() );
    } catch ( final ParseException ex ) {
      throw new WebApplicationException( ex );
    }
  }

  public java.sql.Timestamp getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    if ( timestamp != null ) {
      return timestamp.toString();
    } else {
      return "";
    }
  }
}
