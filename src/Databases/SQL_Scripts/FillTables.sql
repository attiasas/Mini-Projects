-- Basic DATA =============================================================

-- Department -------------------------------------------------------------
INSERT INTO Department (DID,Name,Description,ManagerId)
VALUES (1,'Department1',null, null);
INSERT INTO Department (DID,Name,Description,ManagerId)
VALUES (2,'Department2',null, null);
INSERT INTO Department (DID,Name,Description,ManagerId)
VALUES (3,'Department3',null, null);
---------------------------------------------------------------------------
-- Neighborhood -----------------------------------------------------------
INSERT INTO Neighborhood (NID,Name)
VALUES (1,'Neighborhood1');
INSERT INTO Neighborhood (NID,Name)
VALUES (2,'Neighborhood2');
INSERT INTO Neighborhood (NID,Name)
VALUES (3,'Neighborhood3');
INSERT INTO Neighborhood (NID,Name)
VALUES (4,'Neighborhood4');
INSERT INTO Neighborhood (NID,Name)
VALUES (5,'Neighborhood5');
---------------------------------------------------------------------------
-- Project ----------------------------------------------------------------
INSERT INTO Project (PID,Name,Description,Budget,NeighborhoodID)
VALUES (1,'Project1',null,1000,1);
INSERT INTO Project (PID,Name,Description,Budget,NeighborhoodID)
VALUES (2,'Project2',null,5000,2);
INSERT INTO Project (PID,Name,Description,Budget,NeighborhoodID)
VALUES (3,'Project3',null,2000,2);
INSERT INTO Project (PID,Name,Description,Budget,NeighborhoodID)
VALUES (4,'Project4',null,1000,3);
INSERT INTO Project (PID,Name,Description,Budget,NeighborhoodID)
VALUES (5,'Project5',null,4000,5);
---------------------------------------------------------------------------
-- Apartment -------------------------------------------------------------
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street1',14,1,'Normal',100,1);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street1',13,1,'Normal',100,1);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street2',14,1,'Normal',100,1);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street1',2,2,'Normal',100,4);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street2',43,1,'Normal',60,4);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street3',14,1,'Normal',60,2);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street4',14,4,'Normal',60,5);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street5',23,1,'Normal',60,5);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street6',44,1,'Normal',200,5);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street7',34,1,'Normal',1200,5);
INSERT INTO Apartment (StreetName,Number,Door,type,SizeSquareMeter,NeighborhoodID)
VALUES ('street8',14,1,'Normal',100,2);
---------------------------------------------------------------------------
-- Resident ---------------------------------------------------------------
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (1,'first1','Last1','1-1-1993','street1',14,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (2,'first2','Last1','1-1-1993','street1',14,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (3,'first3','Last1','1-1-1993','street1',14,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (4,'first4','Last2','1-1-1993','street1',13,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (5,'first5','Last2','1-1-1993','street1',13,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (6,'first6','Last3','1-1-1993','street2',14,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (7,'first7','Last4','1-1-1993','street1',2,2);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (8,'first8','Last4','1-1-1993','street1',2,2);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (9,'first9','Last5','1-1-1993','street2',43,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (10,'first10','Last6','1-1-1993','street3',14,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (11,'first11','Last7','1-1-1993','street4',14,4);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (12,'first12','Last7','1-1-1993','street4',14,4);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (13,'first13','Last7','1-1-1993','street4',14,4);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (14,'first14','Last7','1-1-1993','street4',14,4);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (15,'first15','Last8','1-1-1993','street5',23,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (16,'first16','Last8','1-1-1993','street5',23,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (17,'first17','Last9','1-1-1993','street7',34,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (18,'first18','Last9','1-1-1993','street7',34,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (19,'first19','Last9','1-1-1993','street7',34,1);
INSERT INTO Resident (RID,FirstName,LastName,BirthDate,StreetName,Number,door)
VALUES (20,'first20','Last10','1-1-1993','street8',14,1);
---------------------------------------------------------------------------
-- Cars -------------------------------------------------------------
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (1,null,null,null,null,1);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (2,null,null,null,null,2);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (3,null,null,null,null,4);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (4,null,null,null,null,5);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (5,null,null,null,null,6);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (6,null,null,null,null,7);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (7,null,null,null,null,7);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (8,null,null,null,null,8);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (9,null,null,null,null,9);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (10,null,null,null,null,11);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (11,null,null,null,null,15);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (12,null,null,null,null,16);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (13,null,null,null,null,17);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (14,null,null,null,null,18);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (15,null,null,null,null,20);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (16,null,null,null,null,202023130);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (17,null,null,null,null,433223130);
INSERT INTO Cars (CID,CellPhoneNumber,CreditCard,ExpirationDate,ThreeDigits,ID)
VALUES (18,null,null,null,null,37567130);
---------------------------------------------------------------------------
-- ParkingArea ------------------------------------------------------------
INSERT INTO ParkingArea (AID,Name, priceperhour, maxpriceperday, NeighborhoodID)
VALUES (1,'ParkArea1',50,1000,1);
INSERT INTO ParkingArea (AID,Name, priceperhour, maxpriceperday, NeighborhoodID)
VALUES (2,'ParkArea2',100,2000,1);
INSERT INTO ParkingArea (AID,Name, priceperhour, maxpriceperday, NeighborhoodID)
VALUES (3,'ParkArea3',25,800,2);
INSERT INTO ParkingArea (AID,Name, priceperhour, maxpriceperday, NeighborhoodID)
VALUES (4,'ParkArea4',10,100,3);
INSERT INTO ParkingArea (AID,Name, priceperhour, maxpriceperday, NeighborhoodID)
VALUES (5,'ParkArea5',50,2000,4);
---------------------------------------------------------------------------
-- CarParking -------------------------------------------------------------
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (1,'20170618 08:00:00','20170618 09:00:00',1,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (1,'20170618 09:00:00','20170618 10:00:00',2,100);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (1,'20170618 10:00:00','20170618 11:00:00',3,25);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (1,'20170618 11:00:00','20170618 12:00:00',4,10);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (1,'20170618 12:00:00','20170618 13:00:00',5,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (7,'20170618 08:00:00','20170618 09:00:00',1,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (7,'20170618 09:00:00','20170618 10:00:00',2,100);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (7,'20170618 10:00:00','20170618 11:00:00',3,25);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (7,'20170618 11:00:00','20170618 12:00:00',4,10);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (7,'20170618 12:00:00','20170618 13:00:00',5,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (6,'20170618 08:00:00','20170618 09:00:00',5,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (13,'20170618 08:00:00','20170618 09:00:00',1,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (13,'20170618 09:00:00','20170618 10:00:00',2,100);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (13,'20170618 10:00:00','20170618 11:00:00',3,25);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (13,'20170618 11:00:00','20170618 12:00:00',4,10);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (13,'20170618 12:00:00','20170618 13:00:00',5,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (2,'20170618 08:00:00','20170618 12:00:00',1,150);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (8,'20170618 08:00:00','20170618 12:00:00',5,200);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (8,'20170618 13:00:00','20170618 18:00:00',5,250);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (9,'20170618 08:00:00','20170618 09:00:00',5,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (15,'20170618 08:00:00','20170618 11:00:00',3,75);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (3,'20170618 08:00:00','20170618 18:00:00',2,1000);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (3,'20170619 08:00:00','20170619 09:00:00',4,10);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (4,'20170618 08:00:00','20170618 15:00:00',2,700);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (14,'20170618 08:00:00','20170618 11:00:00',1,100);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (5,'20170618 08:00:00','20170618 12:00:00',2,300);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (5,'20170619 08:00:00','20170619 09:00:00',1,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (10,'20170618 08:00:00','20170618 09:00:00',2,100);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (10,'20170618 12:00:00','20170618 13:00:00',3,25);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (12,'20170618 08:00:00','20170618 09:00:00',4,10);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (16,'20170618 08:00:00','20170618 09:00:00',4,10);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (17,'20170618 12:00:00','20170618 13:00:00',5,50);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (17,'20170618 13:00:00','20170618 18:00:00',5,250);
INSERT INTO CarParking (CID,StartTime,EndTime,ParkingAreaID,Cost)
VALUES (16,'20170620 08:00:00','20170621 08:00:00',1,1200);
---------------------------------------------------------------------------
-- Employee ---------------------------------------------------------------
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (1,'Employee1','Official','1981-01-20','street',1,null,'City1');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (2,'Employee2','Official','1982-10-21','street',1,null,'City1');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (3,'Employee3','Official','1990-06-20','street',1,null,'City1');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (4,'Employee4','Official','1990-03-14','street',1,null,'City1');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (5,'Employee5','Official','1991-04-15','street',1,null,'City1');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (6,'Employee6','Official','1977-02-27','street',1,null,'City1');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (7,'Employee7','Official','1969-12-31','street',1,null,'City2');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (8,'Employee8','Official','1979-12-16','street',1,null,'City2');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (9,'Employee9','Official','1992-12-20','street',1,null,'City2');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (10,'Employee10','Official','1986-02-11','street',1,null,'City3');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (11,'Employee1','Constructor','1990-12-31','street',1,null,'City4');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (12,'Employee2','Constructor','1993-12-30','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (13,'Employee3','Constructor','1978-05-24','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (14,'Employee4','Constructor','1969-12-18','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (15,'Employee5','Constructor','1960-03-19','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (16,'Employee6','Constructor','1993-07-02','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (17,'Employee7','Constructor','9999-08-05','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (18,'Employee8','Constructor','9999-10-04','street',1,null,'City5');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (19,'Employee9','Constructor','9999-10-04','street',1,null,'City6');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (20,'Employee10','Constructor','9999-11-03','street',1,null,'City6');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (21,'Employee11','Constructor','9999-11-17','street',1,null,'City6');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (22,'Employee12','Constructor','9999-12-19','street',1,null,'City6');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (23,'Employee13','Constructor','9999-12-21','street',1,null,'City7');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (24,'Employee14','Constructor','9999-06-04','street',1,null,'City7');
INSERT INTO Employee (EID,FirstName,LastName,BirthDate,StreetName,Number,door,City)
VALUES (25,'Employee15','Constructor','9999-03-19','street',1,null,'City7');
---------------------------------------------------------------------------
-- OfficialEmployee -------------------------------------------------------
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (1,'2015-01-01',null,1);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (2,'2015-01-01',null,1);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (3,'2015-01-01',null,1);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (4,'2000-01-01',null,1);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (5,'2001-01-01',null,1);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (6,'1997-01-01',null,2);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (7,'1996-01-01',null,2);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (8,'2012-01-01',null,3);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (9,'2014-01-01',null,3);
INSERT INTO OfficialEmployee (EID,StartDate,Degree,DepartmentId)
VALUES (10,'2013-01-01',null,3);
---------------------------------------------------------------------------
-- ConstructorEmployee -------------------------------------------------------------
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (11,'Company1',1);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (12,'Company1',1);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (13,'Company1',1);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (14,'Company1',1);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (15,'Company2',2);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (16,'Company2',2);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (17,'Company2',2);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (18,'Company3',2);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (19,'Company3',3);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (20,'Company3',3);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (21,'Company4',4);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (22,'Company5',4);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (23,'Company5',5);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (24,'Company5',10);
INSERT INTO ConstructorEmployee (EID,CompanyName,SalaryPerDay)
VALUES (25,'Company5',1);
---------------------------------------------------------------------------
-- ProjectConstructorEmployee ---------------------------------------------
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (1,11,'2015-01-01','2015-05-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (1,12,'2015-01-01','2015-05-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (1,13,'2015-01-01','2015-05-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (1,14,'2015-01-01','2015-05-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (2,11,'2016-02-01','2016-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (2,15,'2016-02-01','2016-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (2,16,'2016-02-01','2016-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (2,20,'2016-02-01','2016-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (3,17,'2014-02-01','2015-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (3,21,'2014-02-01','2015-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (3,22,'2014-02-01','2015-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (3,23,'2014-02-01','2015-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (4,18,'2012-02-01','2013-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (4,24,'2012-02-01','2013-08-01','worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (5,25,'2015-02-01',null,'worker');
INSERT INTO ProjectConstructorEmployee (PID,EID,StartWorkingDate,EndWorkingDate,JobDescription)
VALUES (5,19,'2015-02-01',null,'worker');
---------------------------------------------------------------------------

-- UPDATE -------------------------------------------------------------
UPDATE Department SET ManagerId = 1 WHERE DID = 1;
UPDATE Department SET ManagerId = 6 WHERE DID = 2;
UPDATE Department SET ManagerId = 2 WHERE DID = 3;
---------------------------------------------------------------------------
-- ========================================================================