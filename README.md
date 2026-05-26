# P-Market — Java Desktop (Swing MVC)

Cau truc thu muc **theo Hinh 9.5 giao trinh CNPM** (giong du an `hotel`):

```
pmarket/
├── src/
│   ├── dao/                    ← Controller (ke thua DAO.java)
│   │   ├── DAO.java
│   │   ├── AccountDAO.java
│   │   ├── PostDAO.java
│   │   ├── ReportDAO.java
│   │   ├── ChatRoomDAO.java
│   │   ├── NotificationDAO.java
│   │   ├── AccountStatDAO.java
│   │   ├── DatabaseUtil.java
│   │   └── ...
│   ├── model/                  ← Entity
│   │   ├── Account.java
│   │   ├── Post.java
│   │   ├── Report.java
│   │   ├── SessionManager.java
│   │   └── ...
│   ├── test/
│   │   └── unit/               ← Kiem thu DAO (mau giao trinh)
│   │       └── DbSmokeTest.java
│   ├── view/
│   │   ├── user/               ← Module a, b (dang nhap, quan ly TK)
│   │   │   ├── LoginFrm.java
│   │   │   ├── HomeAdminFrm.java
│   │   │   ├── ManageAccountFrm.java
│   │   │   └── ...
│   │   ├── post/               ← Module c, d, e
│   │   ├── chat/               ← Module f
│   │   ├── report/             ← Module g, h
│   │   ├── stat/               ← Module i
│   │   └── notification/       ← Module k
│   └── Main.java
├── database/                   ← schema.sql tham khao bao cao
├── lib/h2.jar
└── run.bat
```

## Chenh lech so voi lan dau

| Truoc (sai format) | Sau (dung giao trinh) |
|--------------------|------------------------|
| `com.ptit.pmarket.view.*` (1 package) | `view.user`, `view.post`, `view.chat`, ... |
| `src/main/java/...` (Maven chuan) | `src/` (Eclipse chuan) |
| `util/` rieng | `DatabaseUtil` trong `dao/` |

## Chay

```bat
run.bat
```

Hoac import thu muc `pmarket-swing-java` vao **Eclipse** → New Java Project → source folder `src`.

## Tai khoan demo

- Admin: `admin@ptit.edu.vn` / `admin123`
- SV: `anhnv.b21ce009@stu.ptit.edu.vn` / `student123`
