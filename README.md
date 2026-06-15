# X12 AS2 Integration — JHAH

A Java background service that automates the exchange of **X12 EDI 271** (Health Care Eligibility/Benefit Information) messages between a hospital (**JHAH**) and an insurance clearinghouse (**ROBIN**) over the **AS2 secure messaging protocol**.

The system polls the hospital database for pending patient insurance records, builds standards-compliant X12 271 messages, transmits them over AS2 (with 3DES encryption and digital signing), and saves the sent messages back to the database. It also watches an inbox folder for incoming X12 responses and parses them into patient insurance records.

Built on top of the [OpenAS2 / phax as2-server](https://github.com/phax/as2-lib) framework.

---

## What It Does

### Outbound — Send X12 271 Messages

1. A Quartz scheduler triggers every 2 seconds
2. The service queries `ACCUMED_PATIENT_INSURANCE` for records with `X12_PROCEED = 0` (not yet transmitted)
3. Each pending record is processed in a separate thread:
   - Fetches patient demographics from `accumed_patient` (name, DOB, national ID, gender)
   - Fetches insurance details from `accumed_patient_insurance` (policy, insurer, payer code, start/expiry dates)
   - Fetches the patient's MRN from `ACCUMED_PATIENT_FACILITY_MRN`
   - Fetches current date/time from SQL Server
   - Builds a complete **X12 271 EDI message** (transaction set 5010X279)
   - Sends the message over a persistent TCP socket to the AS2 server on port 10080
   - Marks the record `X12_PROCEED = 1` and saves the raw message to `ACCUMED_X12_SENT_MESSAGES`

### Inbound — Receive and Parse X12 Responses

- `HandleMessage` watches a configured inbox directory using Java's `WatchService`
- When a new X12 file arrives, it parses the EDI segments (delimited by `~` and `*`)
- Extracts: insurer name, national ID, policy name, and coverage date from NM1, REF, and DTP segments
- Inserts the insurance record into the patient database

---

## X12 271 Message Structure

The outbound message follows the **ASC X12 005010X279** standard:

| Segment | Purpose |
|---|---|
| `ISA` | Interchange Control Header |
| `GS*HB` | Functional Group (Health Benefit Information) |
| `ST*271` | Transaction Set — Eligibility/Benefit Response |
| `BHT*0022` | Beginning of Hierarchical Transaction |
| `HL*1 / NM1*PR` | Payer (insurer name + payer code) |
| `HL*2 / NM1*1P` | Provider (JHAH, SV*2000035) |
| `HL*3 / NM1*IL` | Subscriber/patient (name + MRN) |
| `REF*SY` | National ID reference |
| `DMG` | Demographics (DOB, gender) |
| `DTP*346` | Eligibility begin date |
| `DTP*347` | Eligibility end date |
| `EB*1` | Eligibility/Benefit — Active Coverage |

---

## AS2 Partnership Configuration

| Setting | Value |
|---|---|
| Sender | ROBIN |
| Receiver | JHAH |
| AS2 URL | `http://192.168.1.103:10080` |
| Encryption | 3DES |
| Signing | MD5 (ROBIN→JHAH), SHA1 (JHAH→ROBIN) |
| MDN | Signed receipt (pkcs7-signature, optional) |
| Certificates | PKCS12 keystore (`config/certs.p12`) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (Maven project) |
| AS2 Framework | OpenAS2 / phax as2-lib v4.4.5 |
| Scheduler | Quartz Scheduler (cron: `0/2 * * ? * *`) |
| Multithreading | Java Threads (one thread per patient record) |
| Database | Microsoft SQL Server (JDBC / jtds driver) |
| EDI | X12 271 — Health Care Eligibility/Benefit Information (5010X279) |
| File Watch | Java `WatchService` (inbox directory monitoring) |

---

## Configuration

Create `app.properties` in the project root:

```properties
URL=jdbc:jtds:sqlserver://<host>/<database>;User=<username>;Password=<password>
path=<path_to_inbox_directory>
ip=<as2_partner_url>
HOST_NAME=localhost
PORT=10080
```

AS2 server configuration lives in `src/main/resources/config/`:

| File | Purpose |
|---|---|
| `config.xml` | AS2 server settings (port, modules, processors) |
| `partnerships.xml` | Partner definitions (ROBIN ↔ JHAH), encryption/signing settings |
| `certs.p12` | PKCS12 keystore with signing certificates |
| `commands.xml` | AS2 server command definitions |

---

## Running the Server

```bash
# Build
mvn clean install -Pwithdep

# Start AS2 server
java -cp "standalone/*" com.helger.as2.app.MainOpenAS2Server src/main/resources/config/config.xml

# The scheduler starts automatically — no additional command needed
```

Or on Windows: run `run.cmd`

---

## Project Structure

```
src/main/java/com/helger/as2/
├── client/
│   ├── MainTestClient.java          # Core: builds X12 271 messages + sends per patient
│   ├── X12Client.java               # Socket-level AS2 message sender
│   ├── HandleMessage.java           # Inbox watcher: parses incoming X12, inserts to DB
│   ├── AuthourizationJob.java       # Quartz scheduler setup (every 2 seconds)
│   ├── AuthorizationJobSchedularController.java  # Quartz job executor
│   ├── PatientRequest.java          # Patient + insurance ID holder (per-thread)
│   ├── Utils.java                   # SQL query constants
│   └── AppProperties.java           # Properties loader
├── app/                             # OpenAS2 server core (AS2 session, cert factory)
├── cmd/                             # AS2 command framework
├── cmdprocessor/                    # Socket/stream command processors
└── util/                            # Utilities (file monitor, byte coder)

src/main/resources/config/
├── config.xml                       # AS2 server config
├── partnerships.xml                 # Partner definitions (ROBIN ↔ JHAH)
├── certs.p12                        # Certificate keystore
└── commands.xml                     # Command definitions
```

---

## Database Tables Used

| Table | Purpose |
|---|---|
| `ACCUMED_PATIENT_INSURANCE` | Source of pending insurance records (`X12_PROCEED = 0`) |
| `accumed_patient` | Patient demographics (name, DOB, national ID, gender) |
| `ACCUMED_PATIENT_FACILITY_MRN` | Patient MRN per facility |
| `ACCUMED_X12_SENT_MESSAGES` | Archive of all sent X12 messages |

---

## ⚠️ Security Note

Do not commit `app.properties` with real database credentials to version control. Add it to `.gitignore` and use environment variables or a secrets manager instead.

---

## License

Built for a client (JHAH / ROBIN integration). Based on [as2-server](https://github.com/phax/as2-lib) by Philip Helger, licensed under FreeBSD License.
