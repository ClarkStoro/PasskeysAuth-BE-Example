# Passkeys Authentication Backend Example

This repository contains the backend implementation for passkeys authentication. It works in conjunction with the [PasskeysAuth KMP App Example](https://github.com/ClarkStoro/PasskeysAuth-KMP-App-Example) to provide an example of a secure authentication flow.
This is a demo project and should not be used in production environments.

## How It Works

The backend handles the following key tasks:

| Task                    | Description                                                            |
| ----------------------- | ---------------------------------------------------------------------- |
| **User Registration**   | Generates and stores on cache the passkey credentials                  |
| **User Authentication** | Verifies the user's identity using passkeys.                           |
| **Token Generation**    | Issues tokens for secure API access                                    |
| **Protected Endpoint**  | Simple list of todos endpoint protected accessible with a Bearer token |
| **Check Status**        | Provides a simple endpoint to check if the backend is running          |

It uses the [WebAuthn4J](https://github.com/webauthn4j/webauthn4j) library for passkeys verification.
Currently, there is only cache data persistence implemented (no database attached).

## Important

The backend includes a **Dockerfile** for easy setup and deployment.
For mobile apps to work correctly, the server must be publicly accessible on the internet and hosted with a valid SSL certificate issued by a trusted CA (Certificate Authority). Without this, authentication on mobile apps will fail.

## Configuration

Before running the backend, make sure to update the following configurations:

- **Allowed Origins:** Set the allowed client origins based on your mobile apps. At the moment, for Android, it is set up with a debug SHA-256.
- **RP\_ID:** Update the Relying Party ID to match your app's domain.
- **apple-app-site-association:** Ensure this file contains the correct app information for iOS passkey support.
- **assetlinks.json:** Adjust this file for Android to link the app and domain correctly.


## Docs

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/)
- [WebAuthn4j Documentation](https://github.com/webauthn4j/webauthn4j)

## License

This project is licensed under the MIT License.
