/*
 Navicat Premium Dump SQL

 Source Server         : MDB-LOCAL
 Source Server Type    : MariaDB
 Source Server Version : 100529 (10.5.29-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : spring_sdk_sample

 Target Server Type    : MariaDB
 Target Server Version : 100529 (10.5.29-MariaDB)
 File Encoding         : 65001

 Date: 20/04/2026 16:23:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category_features
-- ----------------------------
DROP TABLE IF EXISTS `category_features`;
CREATE TABLE `category_features` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for companies
-- ----------------------------
DROP TABLE IF EXISTS `companies`;
CREATE TABLE `companies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `npwp` varchar(50) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `province_id` int(11) DEFAULT NULL,
  `regency_id` int(11) DEFAULT NULL,
  `district_id` int(11) DEFAULT NULL,
  `zip_code` varchar(10) DEFAULT NULL,
  `photo_profile` text DEFAULT NULL,
  `address` text DEFAULT NULL,
  `password_update` tinyint(1) DEFAULT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `status_active` tinyint(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `flag_status` smallint(6) DEFAULT 0,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for company_feature_users
-- ----------------------------
DROP TABLE IF EXISTS `company_feature_users`;
CREATE TABLE `company_feature_users` (
  `company_id` int(11) NOT NULL,
  `feature_id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `quota` int(11) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for company_features
-- ----------------------------
DROP TABLE IF EXISTS `company_features`;
CREATE TABLE `company_features` (
  `company_id` int(11) NOT NULL,
  `feature_id` int(11) NOT NULL,
  `quota` int(11) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `key` varchar(50) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`key`) USING BTREE,
  KEY `idx_key` (`value`,`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for features
-- ----------------------------
DROP TABLE IF EXISTS `features`;
CREATE TABLE `features` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `feature_key` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `category_feature_id` int(11) NOT NULL,
  `veripal_base_url` text DEFAULT NULL,
  `veripal_feature` text DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `role` varchar(50) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for user_branch
-- ----------------------------
DROP TABLE IF EXISTS `user_branch`;
CREATE TABLE `user_branch` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `branch_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `username` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
