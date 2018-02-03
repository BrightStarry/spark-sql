/**
  根据访问数统计TopN视频
 */
CREATE TABLE day_video_access_topn_stat (
  id BIGINT AUTO_INCREMENT,
  day    VARBINARY(8) NOT NULL,
  cms_id BIGINT       NOT NULL,
  times  BIGINT       NOT NULL,
  PRIMARY KEY (id)
);

/**
  统计每个城市访问次数TopN视频
 */
CREATE TABLE day_video_city_access_topn_stat (
  id BIGINT AUTO_INCREMENT,
  day    VARBINARY(8) NOT NULL,
  city VARBINARY(20)       NOT NULL,
  cms_id BIGINT       NOT NULL,
  times  BIGINT       NOT NULL,
  times_rank INT NOT NULL ,
  PRIMARY KEY (id)
);

/**
 根据流量统计TopN视频
*/
CREATE TABLE day_video_traffic_access_topn_stat (
  id BIGINT AUTO_INCREMENT,
  day    VARBINARY(8) NOT NULL,
  cms_id BIGINT       NOT NULL,
  traffic_sum  BIGINT NOT NULL,
  PRIMARY KEY (id)
);