use camundabpmn;
/*
 1.  Add some Camunda Indexes to history schema part (for Archiving)
*/
create INDEX IF NOT EXISTS IDX_ACT_HI_TASKINST_PIID ON ACT_HI_TASKINST (PROC_INST_ID_); 
create INDEX IF NOT EXISTS IDX_ACT_HI_COMMENT_PIID ON ACT_HI_COMMENT (PROC_INST_ID_);
create INDEX IF NOT EXISTS IDX_ACT_HI_ATTACHMENT_PIID ON ACT_HI_ATTACHMENT (PROC_INST_ID_);
create INDEX IF NOT EXISTS IDX_ACT_HI_OP_LOG_PIID ON ACT_HI_OP_LOG (PROC_INST_ID_);
create INDEX IF NOT EXISTS IDX_ACT_HI_INCIDENT_PIID ON ACT_HI_INCIDENT (PROC_INST_ID_);
create INDEX IF NOT EXISTS IDX_ACT_HI_ACTINST_PIID ON ACT_HI_ACTINST(PROC_INST_ID_); 


/*
 2.  Create Archiving Tables in current schema 
*/

/*-- TMP_ARCHIVING_PROCINST */
CREATE TABLE TMP_ARCHIVING_PROCINST
( PROC_INST_ID_ varchar(64) not null,
  END_TIME_ datetime(3)
);
CREATE INDEX AI_TMP_ARCH_PROCINST_PI_ID ON TMP_ARCHIVING_PROCINST(PROC_INST_ID_);

/*-- TMP_ARCHIVING_BYTEARRAY */
CREATE TABLE TMP_ARCHIVING_BYTEARRAY
( BYTEARRAY_ID_ varchar(64) not null,
  PROC_INST_ID_ varchar(64)
);
CREATE INDEX AI_TMP_ARCH_BYTEARRAY_BAID ON TMP_ARCHIVING_BYTEARRAY(BYTEARRAY_ID_);


/*--#1 ARCHIVE_ACT_HI_PROCINST; */
create TABLE ARCHIVE_ACT_HI_PROCINST
AS ( select * from ACT_HI_PROCINST where 1=0);

create index AI_HI_PROCINST_END_TIME on ARCHIVE_ACT_HI_PROCINST(END_TIME_);
ALTER TABLE ARCHIVE_ACT_HI_PROCINST ADD CONSTRAINT  ARCHIVE_ACT_HI_PROCINST_UQ UNIQUE ( PROC_INST_ID_);

/*--#2   ARCHIVE_ACT_HI_ACTINST; */
create TABLE ARCHIVE_ACT_HI_ACTINST
AS ( select * from ACT_HI_ACTINST where 1=0);

create index AI_HI_ACTINST_PROC_INST_ID on ARCHIVE_ACT_HI_ACTINST(PROC_INST_ID_);
create index AI_HI_ACTINST_END_TIME on ARCHIVE_ACT_HI_ACTINST(END_TIME_);

/*--#3  ARCHIVE_ACT_HI_TASKINST; */
create TABLE ARCHIVE_ACT_HI_TASKINST
AS ( select * from ACT_HI_TASKINST where 1=0);

create index AI_HI_TASKINST_PROC_INST_ID on ARCHIVE_ACT_HI_TASKINST(PROC_INST_ID_);
create index AI_HI_TASKINST_END_TIME on ARCHIVE_ACT_HI_TASKINST(END_TIME_);

/*--#4 ARCHIVE_ACT_HI_VARINST; */
create TABLE ARCHIVE_ACT_HI_VARINST
AS ( select * from ACT_HI_VARINST where 1=0);

create index AI_HI_VARINST_PROC_INST_ID on ARCHIVE_ACT_HI_VARINST(PROC_INST_ID_);

/*--#5 ARCHIVE_ACT_HI_DETAIL; */
create TABLE ARCHIVE_ACT_HI_DETAIL
AS ( select * from ACT_HI_DETAIL where 1=0);

create index AI_HI_DETAIL_PROC_INST_ID on ARCHIVE_ACT_HI_DETAIL(PROC_INST_ID_);
create index AI_HI_DETAIL_TIME on ARCHIVE_ACT_HI_DETAIL(TIME_);

/*--#6 ARCHIVE_ACT_HI_COMMENT; */
create TABLE ARCHIVE_ACT_HI_COMMENT
AS ( select * from ACT_HI_COMMENT where 1=0);

create index AI_HI_COMMENT_PROC_INST_ID on ARCHIVE_ACT_HI_COMMENT(PROC_INST_ID_);
create index AI_HI_COMMENT_TIME on ARCHIVE_ACT_HI_COMMENT(TIME_);

/*--#7 ARCHIVE_ACT_HI_ATTACHMENT; */
create TABLE ARCHIVE_ACT_HI_ATTACHMENT
AS ( select * from ACT_HI_ATTACHMENT where 1=0);

create index AI_HI_ATTACHMENT_PROC_INST_ID on ARCHIVE_ACT_HI_ATTACHMENT(PROC_INST_ID_);

/*--#8 ARCHIVE_ACT_HI_OP_LOG; */
create TABLE ARCHIVE_ACT_HI_OP_LOG
AS ( select * from ACT_HI_OP_LOG where 1=0);

create index AI_HI_OP_LOG_PROC_INST_ID on ARCHIVE_ACT_HI_OP_LOG(PROC_INST_ID_);
create index AI_HI_OP_LOG_TIMESTAMP on ARCHIVE_ACT_HI_OP_LOG(TIMESTAMP_);

/*--#9 ARCHIVE_ACT_HI_INCIDENT; */
create TABLE ARCHIVE_ACT_HI_INCIDENT
AS ( select * from ACT_HI_INCIDENT where 1=0);

create index AI_HI_INCIDENT_PROC_INST_ID on ARCHIVE_ACT_HI_INCIDENT(PROC_INST_ID_);

/*--#10 ARCHIVE_ACT_GE_BYTEARRAY; */
create TABLE ARCHIVE_ACT_GE_BYTEARRAY
AS ( select * from ACT_GE_BYTEARRAY where 1=0);

create index AI_GE_BYTEARRAY_ID_ on ARCHIVE_ACT_GE_BYTEARRAY(ID_);

/* -----------------------------------------------------------------------------
Extend a ARCHIVE: Table by two attributes: STAT_EXECUTION_ID, STAT_EXECUTION_TS 
*/
 
/*
--TEMPLATE:
alter table ARCHIVE_%TableName%
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_%TableName%_EXE_ID ON ARCHIVE_%TableName%(STAT_EXECUTION_ID);
*/


/*--#1 ACT_HI_PROCINST */
alter table ARCHIVE_ACT_HI_PROCINST
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_PROCINST_EXE_ID ON ARCHIVE_ACT_HI_PROCINST(STAT_EXECUTION_ID);

/*--#2 ACT_HI_ACTINST */
alter table ARCHIVE_ACT_HI_ACTINST
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_ACTINST_EXE_ID ON ARCHIVE_ACT_HI_ACTINST(STAT_EXECUTION_ID);

/*--#3 ACT_HI_TASKINST */
alter table ARCHIVE_ACT_HI_TASKINST
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_TASKINST_EXE_ID ON ARCHIVE_ACT_HI_TASKINST(STAT_EXECUTION_ID);

/*--#4 ACT_HI_VARINST */
alter table ARCHIVE_ACT_HI_VARINST
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_VARINST_EXE_ID ON ARCHIVE_ACT_HI_VARINST(STAT_EXECUTION_ID);

/*--#5 ACT_HI_DETAIL */
alter table ARCHIVE_ACT_HI_DETAIL
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_DETAIL_EXE_ID ON ARCHIVE_ACT_HI_DETAIL(STAT_EXECUTION_ID);

/*--#6 ACT_HI_COMMENT */
alter table ARCHIVE_ACT_HI_COMMENT
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_COMMENT_EXE_ID ON ARCHIVE_ACT_HI_COMMENT(STAT_EXECUTION_ID);

/*--#7 ACT_HI_ATTACHMENT */
alter table ARCHIVE_ACT_HI_ATTACHMENT
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_ATTACHMENT_EXE_ID ON ARCHIVE_ACT_HI_ATTACHMENT(STAT_EXECUTION_ID);

/*--#8 ACT_HI_OP_LOG */
alter table ARCHIVE_ACT_HI_OP_LOG
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_OP_LOG_EXE_ID ON ARCHIVE_ACT_HI_OP_LOG(STAT_EXECUTION_ID);

/*--#9 ACT_HI_INCIDENT */
alter table ARCHIVE_ACT_HI_INCIDENT
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_HI_INCIDENT_EXE_ID ON ARCHIVE_ACT_HI_INCIDENT(STAT_EXECUTION_ID);

/*--#10 ACT_GE_BYTEARRAY */
alter table ARCHIVE_ACT_GE_BYTEARRAY
    add (STAT_EXECUTION_ID bigint, STAT_EXECUTION_TS timestamp(0) DEFAULT CURRENT_TIMESTAMP );
CREATE INDEX AI_ACT_GE_BYTEARRAY_EXE_ID ON ARCHIVE_ACT_GE_BYTEARRAY(STAT_EXECUTION_ID);


/* -- Next Val as a user defined function needed only in MariaDB--*/
DROP FUNCTION IF EXISTS NextVal;
  DELIMITER //
  CREATE FUNCTION NextVal (vname VARCHAR(30))
    RETURNS INT
  BEGIN
     -- Retrieve and update in single statement
     UPDATE _sequences
       SET next = next + 1
       WHERE name = vname;
 
     RETURN (SELECT next FROM _sequences LIMIT 1);
  END
  //
  DELIMITER ;
  
/* -- History tables for use in archive procedure, there is no array type in MariaDB --*/
Create Table Camunda_Hi_Tables (id_ MEDIUMINT NOT NULL AUTO_INCREMENT,
     TableName_ varchar(80) NOT NULL,
     PRIMARY KEY (id_));
   
Insert Into Camunda_Hi_Tables(TableName_) Values ('ACT_HI_PROCINST');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_ACTINST');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_TASKINST');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_VARINST');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_DETAIL');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_COMMENT');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_ATTACHMENT');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_OP_LOG');
Insert Into Camunda_Hi_Tables(TableName_)  Values ('ACT_HI_INCIDENT');

/*-- log table --*/
CREATE TABLE TMPLOGTABLE (LogMessage Varchar(700));


/* -- Below user defined functions and procedures needed only in MariaDB, they are in-built in Oracle --*/
/*-- Create a sequence SP */
DROP PROCEDURE IF EXISTS CreateSequence;
  DELIMITER //
  CREATE PROCEDURE CreateSequence (name VARCHAR(30), start INT, inc INT)
  BEGIN
     -- Create a table to store sequences
     CREATE TABLE IF NOT EXISTS _sequences
     (
         name VARCHAR(70) NOT NULL UNIQUE,
         next INT NOT NULL,
         inc INT NOT NULL
     );
 
     -- Add the new sequence
     INSERT INTO _sequences VALUES (name, start, inc);  
  END
  //
  DELIMITER ;

/*--------------------------------------------------------------------------------------------------
 Add Meta to Archive
 -------------------------------------------------------------------------------------------------- */

/* Create STAT_EXECUTION_SEQ: each Archive Entry has a same Execution ID during one Archiving Run */
CALL CreateSequence('STAT_EXECUTION_SEQ', 1, 1);
