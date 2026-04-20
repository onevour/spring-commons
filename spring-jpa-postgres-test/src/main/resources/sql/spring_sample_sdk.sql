/*
 Navicat Premium Dump SQL

 Source Server         : LOCAL
 Source Server Type    : PostgreSQL
 Source Server Version : 120012 (120012)
 Source Host           : localhost:5432
 Source Catalog        : spring_sdk_sample
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120012 (120012)
 File Encoding         : 65001

 Date: 16/12/2025 02:26:29
*/


-- ----------------------------
-- Sequence structure for category_feature_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."category_feature_id_seq";
CREATE SEQUENCE "public"."category_feature_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."category_feature_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for companies_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."companies_id_seq";
CREATE SEQUENCE "public"."companies_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."companies_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for features_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."features_id_seq";
CREATE SEQUENCE "public"."features_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."features_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for user_branch_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."user_branch_id_seq";
CREATE SEQUENCE "public"."user_branch_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."user_branch_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."user_id_seq";
CREATE SEQUENCE "public"."user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."user_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for category_features
-- ----------------------------
DROP TABLE IF EXISTS "public"."category_features";
CREATE TABLE "public"."category_features" (
  "id" int4 NOT NULL DEFAULT nextval('category_feature_id_seq'::regclass),
  "key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."category_features" OWNER TO "postgres";

-- ----------------------------
-- Records of category_features
-- ----------------------------
BEGIN;
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 'OCR', 'OCR', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 'FACE_RECOGNITION_VERIFICATION', 'Face Recognition Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (3, 'BIOMETRIC_VERIFICATION', 'Biometric Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (4, 'EKYB_VERIFICATION', 'EKYB Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (5, 'TAX_VERIFICATION', 'Tax Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (6, 'PHONE_VERIFICATION', 'Phone Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (7, 'NEGATIVE_RECORD_VERIFICATION', 'Negative Record Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (8, 'LOCATION_VERIFICATION', 'Location Verification', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."category_features" ("id", "key", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (9, 'GET_NUMBER', 'Get Number', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for companies
-- ----------------------------
DROP TABLE IF EXISTS "public"."companies";
CREATE TABLE "public"."companies" (
  "id" int4 NOT NULL DEFAULT nextval('companies_id_seq'::regclass),
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "npwp" varchar(50) COLLATE "pg_catalog"."default",
  "email" varchar(255) COLLATE "pg_catalog"."default",
  "phone" varchar(50) COLLATE "pg_catalog"."default",
  "province_id" int4,
  "regency_id" int4,
  "district_id" int4,
  "zip_code" varchar(10) COLLATE "pg_catalog"."default",
  "photo_profile" text COLLATE "pg_catalog"."default",
  "address" text COLLATE "pg_catalog"."default",
  "password_update" bool,
  "slug" varchar(255) COLLATE "pg_catalog"."default",
  "token" varchar(255) COLLATE "pg_catalog"."default",
  "status_active" bool,
  "description" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "flag_status" int2 DEFAULT 0,
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."companies" OWNER TO "postgres";

-- ----------------------------
-- Records of companies
-- ----------------------------
BEGIN;
INSERT INTO "public"."companies" ("id", "name", "npwp", "email", "phone", "province_id", "regency_id", "district_id", "zip_code", "photo_profile", "address", "password_update", "slug", "token", "status_active", "description", "flag_status", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 'PT. ASLI RANCANGAN INDONESIA', '12345678910', 'admin@asliri.id', '8117799888', 31, 3174, 317407, '12130', NULL, 'Bulungan Business Center, Jl. Bulungan No.15 6, RT.6/RW.6, Kramat Pela', 'f', 'internal', 'xx', 't', 'PT. ASLIRI', 1, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."companies" ("id", "name", "npwp", "email", "phone", "province_id", "regency_id", "district_id", "zip_code", "photo_profile", "address", "password_update", "slug", "token", "status_active", "description", "flag_status", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 'PT. TESTER', '12345678910', 'admin@tester.id', '81234567890', 32, 3201, 320101, '', NULL, NULL, 'f', 'internalx2', 'xx', 't', 'PT. TESTER', 1, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for company_feature_users
-- ----------------------------
DROP TABLE IF EXISTS "public"."company_feature_users";
CREATE TABLE "public"."company_feature_users" (
  "company_id" int4 NOT NULL,
  "feature_id" int4 NOT NULL,
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "quota" int4,
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."company_feature_users" OWNER TO "postgres";

-- ----------------------------
-- Records of company_feature_users
-- ----------------------------
BEGIN;
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 1, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 2, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 3, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 4, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 5, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 6, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 7, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 8, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 9, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 10, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 11, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 12, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 13, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 14, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 15, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 16, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 17, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 18, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 19, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 20, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 21, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 22, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 23, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 24, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 25, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 26, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 27, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 28, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 29, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 30, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 31, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 32, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 33, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 34, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 35, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 36, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 37, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 38, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 39, 'superadmin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 1, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 2, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 3, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 4, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 5, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 6, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 7, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 8, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 9, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 10, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 11, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 12, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 13, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 14, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 15, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 16, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 17, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 18, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 19, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 20, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 21, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 22, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 23, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 24, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 25, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 26, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 27, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 28, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 29, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 30, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 31, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 32, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 33, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 34, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 35, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 36, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 37, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 38, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 39, 'admin@asliri.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 1, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 2, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 3, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 4, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 5, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 6, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 7, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 8, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 9, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 10, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 11, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 12, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 13, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 14, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 15, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 16, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 17, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 18, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 19, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 20, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 21, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 22, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 23, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 24, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 25, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 26, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 27, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 28, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 29, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 30, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 31, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 32, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 33, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 34, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 35, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 36, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 37, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 38, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 39, 'admin@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 1, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 2, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 3, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 4, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 5, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 6, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 7, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 8, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 9, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 10, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 11, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 12, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 13, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 14, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 15, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 16, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 17, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 18, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 19, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 20, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 21, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 22, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 23, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 24, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 25, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 26, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 27, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 28, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 29, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 30, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 31, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 32, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 33, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 34, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 35, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 36, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 37, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 38, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_feature_users" ("company_id", "feature_id", "username", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 39, 'user@tester.id', 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for company_features
-- ----------------------------
DROP TABLE IF EXISTS "public"."company_features";
CREATE TABLE "public"."company_features" (
  "company_id" int4 NOT NULL,
  "feature_id" int4 NOT NULL,
  "quota" int4,
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."company_features" OWNER TO "postgres";

-- ----------------------------
-- Records of company_features
-- ----------------------------
BEGIN;
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 1, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 2, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 3, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 4, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 5, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 6, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 7, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 8, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 9, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 10, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 11, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 12, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 13, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 14, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 15, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 16, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 17, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 18, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 19, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 20, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 21, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 22, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 23, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 24, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 25, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 26, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 27, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 28, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 29, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 30, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 31, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 32, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 33, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 34, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 35, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 36, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 37, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 38, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 39, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 1, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 2, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 3, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 4, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 5, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 6, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 7, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 8, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 9, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 10, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 11, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 12, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 13, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 14, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 15, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 16, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 17, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 18, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 19, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 20, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 21, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 22, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 23, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 24, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 25, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 26, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 27, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 28, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 29, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 30, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 31, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 32, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 33, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 34, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 35, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 36, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 37, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 38, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."company_features" ("company_id", "feature_id", "quota", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 39, 1000, 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for features
-- ----------------------------
DROP TABLE IF EXISTS "public"."features";
CREATE TABLE "public"."features" (
  "id" int4 NOT NULL DEFAULT nextval('features_id_seq'::regclass),
  "feature_key" varchar(255) COLLATE "pg_catalog"."default",
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "sequence" int4,
  "category_feature_id" int4 NOT NULL,
  "veripal_base_url" text COLLATE "pg_catalog"."default",
  "veripal_feature" text COLLATE "pg_catalog"."default",
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."features" OWNER TO "postgres";

-- ----------------------------
-- Records of features
-- ----------------------------
BEGIN;
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (2, 'AUTO_FACE_CROP', 'Auto Face Crop', 0, 2, 'https://api.asliri.id:8443', '/auto_face_crop', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (3, 'VERIFY_FACE_MATCH', 'Face Match Verification', 0, 2, 'https://api.asliri.id:8443', '/verify_face_match', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (4, 'PASSIVE_LIVENESS', 'Passive Liveness', 0, 2, 'https://api.asliri.id:8443', '/passive_liveness', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (6, 'VERIFY_BIOMETRIC_PLUS', 'Biometric Plus Verification', 0, 3, 'https://api.biometricverification.net', '/verify_biometric_plus', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (7, 'VERIFY_BIOMETRIC_BASIC', 'Biometric Basic Verification', 0, 3, 'https://api.biometricverification.net', '/verify_biometric_basic', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (8, 'VERIFY_BIOMETRIC_BASIC_PLUS', 'Biometric Basic Plus Verification', 0, 3, 'https://api.biometricverification.net', '/verify_biometric_basic_plus', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (9, 'VERIFY_PROFESSIONAL', 'Professional Verification', 0, 3, 'https://api.biometricverification.net', '/verify_professional', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (10, 'VERIFY_PROFESSIONAL_V2', 'Professional V2 Verification', 0, 3, 'https://api.biometricverification.net', '/verify_professional_v2', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (11, 'VERIFY_PLATINUM', 'Platinum Verification', 0, 3, 'https://api.biometricverification.net', '/verify_platinum', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (1, 'OCR_EXTRA', 'OCR Extra', 0, 1, 'https://api.asliri.id:8443', '/ocr_extra', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (5, 'VERIFY_BIOMETRIC', 'Biometric Verification', 0, 3, 'https://api.biometricverification.net', '/verify_biometric', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (39, 'GET_TAGS', 'Get Tags', 0, 9, 'https://api.asliri.id:8443', '/get_contact_tags', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (38, 'GET_NAME', 'Get Name', 0, 9, 'https://api.asliri.id:8443', '/get_contact_name', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (12, 'SK_COMPANY', 'SK Company Verification', 0, 4, 'https://api.asliservices.com:8443', '/sk_company', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (13, 'SK_COMPANY_MODAL', 'SK Company Modal Verification', 0, 4, 'https://api.asliservices.com:8443', '/sk_company_modal', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (14, 'SK_COMPANY_POSITION', 'SK Company Position Verification', 0, 4, 'https://api.asliservices.com:8443', '/sk_company_position', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (15, 'SK_COMPANY_SHAREHOLDER', 'SK Company Shareholder Verification', 0, 4, 'https://api.asliservices.com:8443', '/sk_company_shareholder', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (16, 'VERIFY_COMPANY_HEADCOUNT', 'Company Headcount Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_company_headcount', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (17, 'VERIFY_COMPANY_OWNERSHIP', 'Company Ownership Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_company_ownership', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (18, 'VERIFY_TAX_COMPANY', 'Tax Company Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_tax_company', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (19, 'VERIFY_COMPANY_VALUE', 'Company Value Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_company_value', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (20, 'VERIFY_COMPANY_RATIO', 'Company Ratio Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_company_ratio', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (21, 'VERIFY_INCOME', 'Income (NIK) Verification', 0, 5, 'https://api.asliservices.com:8443', '/verify_income', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (22, 'VERIFY_INCOME_GRADE', 'Income Grade Verification', 0, 5, 'https://api.asliservices.com:8443', '/verify_income_grade', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (23, 'VERIFY_INCOME_GRADEV2', 'Income Grade V2 Verification', 0, 5, 'https://api.asliservices.com:8443', '/verify_income_gradev2', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (24, 'VERIFY_PHONE', 'Phone Verification', 0, 6, 'https://api.asliservices.com:8443', '/verify_phone', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (25, 'VERIFY_PHONE_AGE', 'Phone Age Verification', 0, 6, 'https://api.asliservices.com:8443', '/verify_phone_age', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (26, 'VERIFY_PHONE_EXTRA', 'Phone Extra Verification', 0, 6, 'https://api.asliservices.com:8443', '/verify_phone_extra', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (27, 'VERIFY_PHONE_IDENTITY', 'Phone Identity Verification', 0, 6, 'https://api.asliservices.com:8443', '/verify_phone_identity', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (29, 'VERIFY_TAX_PERSONAL', 'Tax Personal Verification', 0, 5, 'https://api.asliservices.com:8443', '/verify_tax_personal', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (30, 'VERIFY_NPWP', 'Income (NPWP) Verification', 0, 5, 'https://api.asliservices.com:8443', '/verify_npwp', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (31, 'VERIFY_NEGATIVE_LIST', 'Negative List Verification', 0, 7, 'https://api.asliservices.com:8443', '/verify_negative_list', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (32, 'VERIFY_WORKPLACE', 'Workplace Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_workplace', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (33, 'VERIFY_WORKPLACE_PERCENTAGE', 'Workplace Percentage Verification', 0, 4, 'https://api.asliservices.com:8443', '/verify_workplace_percentage', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (34, 'VERIFY_HOME_ADDRESS', 'Home Address Verification', 0, 8, 'https://api.asliservices.com:8443', '/verify_home_address', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (35, 'VERIFY_WORK_ADDRESS', 'Work Address Verification', 0, 8, 'https://api.asliservices.com:8443', '/verify_work_address', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (36, 'VERIFY_SCORE_HOME_ADDRESS', 'Score Home Address Verification', 0, 8, 'https://api.asliservices.com:8443', '/verify_score_home_address', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (37, 'VERIFY_SCORE_WORK_ADDRESS', 'Score Work Address Verification', 0, 8, 'https://api.asliservices.com:8443', '/verify_score_work_address', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."features" ("id", "feature_key", "name", "sequence", "category_feature_id", "veripal_base_url", "veripal_feature", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES (28, 'VERIFY_TAX_EXTRA', 'Tax Extra Verification', 0, 5, 'https://api.asliservices.com:8443', '/verify_tax_extra', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS "public"."roles";
CREATE TABLE "public"."roles" (
  "role" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."roles" OWNER TO "postgres";

-- ----------------------------
-- Records of roles
-- ----------------------------
BEGIN;
INSERT INTO "public"."roles" ("role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('ADMIN', 'f', 'system', '2025-11-30 04:11:15.113', 'system', '2025-11-30 04:11:15.113');
INSERT INTO "public"."roles" ("role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('CLIENT', 'f', 'system', '2025-11-30 04:18:22.397', 'system', '2025-11-30 04:18:22.397');
INSERT INTO "public"."roles" ("role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('SUPERADMIN', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."roles" ("role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('ADMIN_PLATFORM', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."roles" ("role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('USER_PLATFORM', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."roles" ("role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('USER', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for user_branch
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_branch";
CREATE TABLE "public"."user_branch" (
  "id" int4 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6),
  "branch_id" int4 NOT NULL
)
;
ALTER TABLE "public"."user_branch" OWNER TO "postgres";

-- ----------------------------
-- Records of user_branch
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_roles";
CREATE TABLE "public"."user_roles" (
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "role" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."user_roles" OWNER TO "postgres";

-- ----------------------------
-- Records of user_roles
-- ----------------------------
BEGIN;
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('demo', 'ADMIN', 'f', 'system', '2025-11-30 21:10:13.995', 'system', '2025-11-30 21:40:19.846');
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('superadmin@asliri.id', 'SUPERADMIN', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('superadmin@asliri.id', 'ADMIN_PLATFORM', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('superadmin@asliri.id', 'USER_PLATFORM', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('superadmin@asliri.id', 'ADMIN', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('superadmin@asliri.id', 'USER', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@asliri.id', 'ADMIN_PLATFORM', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@asliri.id', 'USER_PLATFORM', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@asliri.id', 'ADMIN', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@asliri.id', 'USER', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@tester.id', 'ADMIN', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@tester.id', 'USER', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
INSERT INTO "public"."user_roles" ("username", "role", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('user@tester.id', 'USER', 'f', 'system', '2025-12-01 15:43:30.662832', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS "public"."users";
CREATE TABLE "public"."users" (
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default",
  "is_deleted" bool DEFAULT false,
  "created_by" varchar(255) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "modified_by" varchar(255) COLLATE "pg_catalog"."default",
  "modified_date" timestamp(6)
)
;
ALTER TABLE "public"."users" OWNER TO "postgres";

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO "public"."users" ("username", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('demo', 'update name', 'f', 'system', '2025-11-30 21:10:13.904', 'system', '2025-11-30 21:40:19.88');
INSERT INTO "public"."users" ("username", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('superadmin@asliri.id', 'a', 'f', 'SUPERADMIN ASLIRI', '2025-12-16 02:19:02', 'ASLIRI', NULL);
INSERT INTO "public"."users" ("username", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@asliri.id', 'v', 'f', 'ADMIN ASLIRI', '2025-12-16 02:19:06', 'ASLIRI', NULL);
INSERT INTO "public"."users" ("username", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('admin@tester.id', 'f', 'f', 'ADMIN TESTER', '2025-12-16 02:19:11', 'TESTER', NULL);
INSERT INTO "public"."users" ("username", "name", "is_deleted", "created_by", "created_date", "modified_by", "modified_date") VALUES ('user@tester.id', 'g', 'f', 'USER TESTER', '2025-12-16 02:19:15', 'TESTER', NULL);
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."category_feature_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."companies_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."features_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."user_branch_id_seq"', 66, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."user_id_seq"', 517, true);

-- ----------------------------
-- Primary Key structure for table category_features
-- ----------------------------
ALTER TABLE "public"."category_features" ADD CONSTRAINT "category_feature_id_pk" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table companies
-- ----------------------------
ALTER TABLE "public"."companies" ADD CONSTRAINT "companies_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table company_feature_users
-- ----------------------------
ALTER TABLE "public"."company_feature_users" ADD CONSTRAINT "company_feature_users_pkey" PRIMARY KEY ("company_id", "feature_id", "username");

-- ----------------------------
-- Primary Key structure for table company_features
-- ----------------------------
ALTER TABLE "public"."company_features" ADD CONSTRAINT "company_features_pkey" PRIMARY KEY ("company_id", "feature_id");

-- ----------------------------
-- Primary Key structure for table features
-- ----------------------------
ALTER TABLE "public"."features" ADD CONSTRAINT "features_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table user_branch
-- ----------------------------
ALTER TABLE "public"."user_branch" ADD CONSTRAINT "users_copy1_pkey" PRIMARY KEY ("id", "branch_id");

-- ----------------------------
-- Primary Key structure for table user_roles
-- ----------------------------
ALTER TABLE "public"."user_roles" ADD CONSTRAINT "user_roles_pkey" PRIMARY KEY ("username", "role");

-- ----------------------------
-- Indexes structure for table users
-- ----------------------------
CREATE INDEX "finx" ON "public"."users" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table users
-- ----------------------------
ALTER TABLE "public"."users" ADD CONSTRAINT "users_pkey" PRIMARY KEY ("username");

-- ----------------------------
-- Foreign Keys structure for table company_feature_users
-- ----------------------------
ALTER TABLE "public"."company_feature_users" ADD CONSTRAINT "company_feature_users_company_id_fkey" FOREIGN KEY ("company_id") REFERENCES "public"."companies" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."company_feature_users" ADD CONSTRAINT "company_feature_users_feature_id_fkey" FOREIGN KEY ("feature_id") REFERENCES "public"."features" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."company_feature_users" ADD CONSTRAINT "company_feature_users_username_fkey" FOREIGN KEY ("username") REFERENCES "public"."users" ("username") ON DELETE NO ACTION ON UPDATE NO ACTION;
