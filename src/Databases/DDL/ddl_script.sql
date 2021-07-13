--Authors: Amir Gabay ID: 205381684, Assaf Attias ID: 308214899
Create Database [205381684_308214899]
GO
USE [205381684_308214899]
GO

CREATE TABLE Employee
(
EID	integer Primary Key,
FirstName varchar(50) not null,
LastName varchar(50) not null,
BirthDate datetime not null,
City varchar(50) not null,
StreetName varchar(50) not null,
Number integer not null check(Number>0),
Door integer not null
);

CREATE TABLE Cellphones
(
EID integer Foreign Key references Employee(EID) ON DELETE cascade ON UPDATE cascade,
CellPhoneNumber integer not null,
CONSTRAINT CellPhonesPK Primary Key (EID, CellPhoneNumber)
);

CREATE TABLE OfficialEmployee
(
EID integer Primary Key Foreign Key references Employee(EID) ON DELETE cascade ON UPDATE cascade,
StartWorkingDate datetime not null,
Degree varchar(50) not null,
WorkAt integer not null
);

CREATE TABLE ConstructorEmployee
(
EID integer Primary Key Foreign Key references Employee(EID) ON DELETE cascade ON UPDATE cascade,
CompanyName varchar(50) not null,
SalaryPerDay integer not null check (SalaryPerDay>=0)
);

CREATE TABLE Department
(
DID integer Primary Key,
Name varchar(50) not null,
Description varchar(200),
Manager integer not null Foreign Key references OfficialEmployee(EID) ON DELETE no action ON UPDATE cascade
);

ALTER TABLE OfficialEmployee
ADD Constraint FKDepartment Foreign Key (WorkAt) references Department(DID) ON DELETE cascade;

CREATE TABLE Neighborhood
(
NID integer Primary Key,
Name varchar(50) not null
);

CREATE TABLE Project
(
PID integer Primary Key,
Name varchar(50) not null,
Description varchar(200),
Budget integer default 0 check(Budget>=0) not null,
DoneAt integer not null Foreign Key references Neighborhood(NID) ON UPDATE no action ON DELETE no action
);

CREATE TABLE ProjectConstructorEmployee
(
EID integer Foreign Key references ConstructorEmployee(EID) ON DELETE no action ON UPDATE cascade,
PID integer Foreign Key references Project(PID),
StartWorkingDate datetime not null,
EndWorkingDate datetime,
JobDescription varchar(200),
Constraint ProjectConstructorEmployeePK Primary Key(EID, PID)
);

CREATE TABLE Apartment
(
StreetName varchar(50),
Number integer check(Number>0),
Door varchar(10),
Located integer not null Foreign Key references Neighborhood(NID) ON UPDATE no action ON DELETE no action,
Type varchar(50) not null,
SizeSquareMeter float not null default 0 check(SizeSquareMeter >= 0),
Constraint ApartmentPK Primary Key (StreetName, Number, Door)
);

CREATE TABLE Resident
(
RID integer Primary Key,
FirstName varchar(50) not null,
LastName varchar(50) not null,
BirthDate datetime not null,
StreetName varchar(50) not null,
Number integer not null check(Number>0),
Door varchar(10) not null,
Constraint ResAptFK Foreign Key (StreetName, Number, Door) references Apartment(StreetName, Number, Door) ON DELETE no action ON UPDATE cascade
);

CREATE TABLE ParkingArea
(
AID integer Primary Key,
Name varchar(50) not null,
Located integer not null Foreign Key references Neighborhood(NID) ON DELETE cascade ON UPDATE cascade,
PricePerHour float not null default 0 check(PricePerHour>=0),
MaxPricePerDay float not null default 0 check(MaxPricePerDay>=0)
);

CREATE TABLE Cars
(
CID integer Primary Key,
CellPhoneNumber varchar(11) not null,
CreditCard varchar(20) not null,
ExpirationDate datetime not null,
ThreeDigits varchar(3) not null,
ID varchar(10) not null
);

CREATE TABLE CarParking
(
StartTime datetime not null,
EndTime datetime,
Cost float check (Cost>=0),
ParkAt integer Foreign Key references ParkingArea(AID) ON DELETE set null ON UPDATE cascade,
CarParking integer not null Foreign Key references Cars(CID) ON DELETE cascade ON UPDATE cascade,
Constraint CarParkingPK Primary Key (CarParking, StartTime),
Constraint EndStartCh check (EndTime>StartTime)
);