# P-Market - Java Swing MVC

Ung dung desktop Java Swing quan ly cho mua ban do dung sinh vien. Project dang duoc to chuc theo kieu package trong giao trinh: `dao`, `model`, `view.<module>`, `test.unit`.

## Cau truc project

```text
cnpm/
|-- src/
|   |-- Main.java
|   |-- dao/                 # Lop truy cap CSDL va khoi tao schema
|   |-- model/               # Entity/model
|   |-- test/unit/           # Test smoke don gian cho DAO
|   `-- view/
|       |-- category/
|       |-- chat/
|       |-- notification/
|       |-- post/
|       |-- report/
|       |-- stat/
|       `-- user/
|-- database/                # SQL tham khao cho bao cao
|-- lib/h2.jar               # Driver H2 dung khi chay bang run.bat
|-- uploads/                 # Anh upload khi test
|-- pom.xml                  # Cau hinh Maven
`-- run.bat                  # Build va chay nhanh tren Windows
```

Thu muc `build/`, `target/` va cac file database trong `data/` la file sinh ra khi build/chay, khong can dua vao source.

## Yeu cau

- JDK 17 tro len
- Maven 3.x neu muon chay bang Maven
- Windows PowerShell de `run.bat` tu tai `lib/h2.jar` khi may chua co file nay

Kiem tra Java:

```bat
java -version
javac -version
```

## Cach chay nhanh tren Windows

```bat
run.bat
```

Script se:

1. Tai H2 driver vao `lib/h2.jar` neu chua co.
2. Compile lai toan bo file `.java` trong `src`.
3. Chay class `Main`.

## Chay smoke test DAO

```bat
run.bat test
```

Lenh nay compile source va chay `test.unit.DbSmokeTest` de thu dang nhap tai khoan admin.

## Chay bang Maven

```bat
mvn clean compile exec:java
```

## Tai khoan demo

```text
Admin: admin@ptit.edu.vn / admin123
Sinh vien: anhnv.b21ce009@stu.ptit.edu.vn / student123
```

## Ghi chu ve CSDL

Ung dung dung H2 file database tai `data/pmarket.mv.db`. Neu file database chua ton tai, chuong trinh se tu tao schema va seed du lieu mau trong lan chay dau tien.
