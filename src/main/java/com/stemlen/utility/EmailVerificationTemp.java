package com.stemlen.utility;

public class EmailVerificationTemp {
    public static String getMessageBody(String verificationLink, String name) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Email Verification</title>
                    <style>
                        body {
                            font-family: 'Nunito Sans', sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .email-container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border: 1px solid #e0e0e0;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        }
                        .email-header {
                            background-color: #0056b3;
                            color: #ffffff;
                            padding: 20px;
                            text-align: center;
                            font-size: 22px;
                            font-weight: 600;
                        }
                        .email-body {
                            padding: 30px;
                            color: #333333;
                            font-size: 16px;
                            line-height: 1.6;
                        }
                        .email-body p {
                            margin: 0 0 15px;
                        }
                        .verification-link {
                            display: inline-block;
                            margin: 20px 0;
                            padding: 10px 20px;
                            font-size: 16px;
                            font-weight: bold;
                            color: #0056b3;
                            background-color: #f0f4ff;
                            border: 1px solid #d0e2ff;
                            border-radius: 4px;
                            text-decoration: none;
                        }
                        .email-footer {
                            background-color: #f9f9f9;
                            padding: 20px;
                            text-align: center;
                            font-size: 14px;
                            color: #888888;
                            border-top: 1px solid #e0e0e0;
                        }
                        .email-footer a {
                            color: #0056b3;
                            text-decoration: none;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-header">Email Verification</div>
                        <div class="email-body">
                            <p>Dear %s,</p>
                            <p>Thank you for signing up! Please verify your email address by clicking the link below:</p>
                            <a href="%s" class="verification-link">Verify Email</a>
                            <p>This link is valid for the next 24 hours. If you did not request this, please disregard this email or contact our support team immediately.</p>
                        </div>
                        <div class="email-footer">
                            <p>Thank you,<br>Stemlen Team</p>
                            <p><a href="https://www.stemlen.com/support">Contact Support</a></p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name, verificationLink);
    }
}