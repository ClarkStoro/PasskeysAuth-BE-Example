# 🔐 Passkeys Authentication Backend

A complete backend implementation for **passkey authentication** using Kotlin and Ktor. This works with the companion [PasskeysAuth KMP App](https://github.com/ClarkStoro/PasskeysAuth-KMP-App-Example) to demonstrate secure, passwordless authentication.

> **⚠️ Demo Project**: This is for educational/demo purposes. Do not use in production without proper security review.

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Kotlin** knowledge
- **Public HTTPS domain** (required for passkeys)

### 1. Clone & Deploy

```bash
git clone <your-backend-repo-url>
cd passkeysauth-be-example
```

**Deploy to Render.com** (for free):
1. Fork this repository
2. Connect to [Render.com](https://render.com)
3. Create new **Web Service**
4. Connect your forked repo
5. Render will auto-detect Dockerfile and deploy!

Your backend will be available at: `https://your-app-name.onrender.com`

### 2. Configure for Your App

#### **Update Domain Configuration**
Edit `src/main/resources/application.yaml`:
```yaml
jwt:
  domain: "https://YOUR-BACKEND-DOMAIN.onrender.com/"  # ← Change this
  audience: "default-audience"
  realm: "Passkeys Ktor Demo"
```

#### **Update Relying Party ID**
Edit `src/main/kotlin/data/repositories/AuthRepositoryImpl.kt`:
```kotlin
companion object {
    private const val RP_ID = "YOUR-BACKEND-DOMAIN.onrender.com"  // ← Change this
    private const val RP_NAME = "Your App Name"                   // ← Change this
}
```

## 🔧 Android App Integration

### Get Your App's Fingerprint
```bash
# For debug keystore (development)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### Update Asset Links
Edit `src/main/resources/static/.well-known/assetlinks.json`:
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls", "delegate_permission/common.get_login_creds"],
    "target": {
      "namespace": "android_app",
      "package_name": "com.yourcompany.yourapp",           // ← Change this
      "sha256_cert_fingerprints": [
        "YOUR:SHA256:FINGERPRINT:HERE"                     // ← Change this
      ]
    }
  }
]
```

### Update Backend Code
Edit `src/main/kotlin/data/repositories/AuthRepositoryImpl.kt`:
```kotlin
// Replace debug fingerprint with yours
private const val ANDROID_APP_FINGERPRINT = "YOUR:SHA256:FINGERPRINT:HERE"
```

## 🍎 iOS App Integration

### Update App Association
Edit `src/main/resources/static/.well-known/apple-app-site-association`:
```json
{
  "webcredentials": {
    "apps": ["YOUR_TEAM_ID.com.yourcompany.yourapp"]      // ← Change this
  }
}
```

## 📱 App Configuration

In your companion mobile app, update the backend URL:

**Android/iOS (NetworkClient.kt)**:
```kotlin
companion object {
    private const val BASE_URL = "https://YOUR-BACKEND-DOMAIN.onrender.com/"  // ← Change this
}
```

## 🏗️ Architecture

| Component               | Purpose                                                    |
| ----------------------- | ---------------------------------------------------------- |
| **User Registration**   | Creates passkey credentials using WebAuthn4J               |
| **User Authentication** | Verifies passkeys and issues JWT tokens                    |
| **Protected Endpoints** | Demo todos API secured with Bearer tokens                  |
| **Asset Links**         | Android/iOS app verification for passkeys                  |
| **CORS Configuration**  | Enables cross-origin requests from mobile apps             |

## 🧪 Testing Your Deployment

### 1. Check Backend Health
```bash
curl https://YOUR-BACKEND-DOMAIN.onrender.com/checkStatus
# Expected: {"status":"OK","isProd":"true"}
```

### 2. Verify Asset Links
```bash
# Android
curl https://YOUR-BACKEND-DOMAIN.onrender.com/.well-known/assetlinks.json

# iOS  
curl https://YOUR-BACKEND-DOMAIN.onrender.com/.well-known/apple-app-site-association
```

### 3. Test Registration
Use your mobile app to register a new user - should work seamlessly!

## 🔒 Security Features

- ✅ **WebAuthn4J** - Industry-standard passkey verification
- ✅ **JWT Tokens** - Secure API authentication  
- ✅ **Origin Validation** - Prevents unauthorized app access
- ✅ **HTTPS Required** - Ensures secure communication
- ✅ **No Password Storage** - Eliminates password-related vulnerabilities

## 🚨 Troubleshooting

### "Registration Failed" in App
- ✅ Check your domain is HTTPS with valid SSL
- ✅ Verify `assetlinks.json` has correct package name & fingerprint
- ✅ Ensure `RP_ID` matches your domain exactly

### "Network Error" in App  
- ✅ Update `BASE_URL` in mobile app to your deployed domain
- ✅ Check CORS configuration allows your app's requests
- ✅ Verify backend is deployed and running

### iOS Passkeys Not Working
- ✅ Update `apple-app-site-association` with correct Team ID
- ✅ Ensure iOS app has proper associated domains entitlement
- ✅ Test on device (simulator has limitations)

## 📚 Learn More

- [WebAuthn Guide](https://webauthn.guide/) - Understanding passkeys
- [Ktor Documentation](https://ktor.io/) - Backend framework
- [WebAuthn4J](https://github.com/webauthn4j/webauthn4j) - Passkey verification library

## 🎯 Demo Ready!

Your backend is now configured for your own passkey demo! 

**Next Steps:**
1. ✅ Deploy this backend to Render.com  
2. ✅ Clone and configure the mobile app
3. ✅ Update app to point to your backend
4. ✅ Test registration and login flow
5. 🎉 **Present your passwordless future!**

---

## 📄 License

MIT License - Feel free to use this for your own demos and learning!
