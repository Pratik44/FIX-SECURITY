#!/usr/bin/env python3
"""
FIX Security Platform REST API
Provides REST endpoints for querying FIX messages, security alerts, and compliance status
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, timedelta
import json
import os

app = Flask(__name__)
CORS(app)

# In-memory storage for demo (replace with database in production)
messages_store = []
alerts_store = []
compliance_results = []

@app.route('/api/v1/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'timestamp': datetime.now().isoformat(),
        'version': '1.0.0'
    })

@app.route('/api/v1/messages', methods=['GET'])
def get_messages():
    """Get FIX messages with filtering"""
    try:
        # Query parameters
        session_id = request.args.get('session_id')
        msg_type = request.args.get('msg_type')
        start_time = request.args.get('start_time')
        end_time = request.args.get('end_time')
        limit = int(request.args.get('limit', 100))
        offset = int(request.args.get('offset', 0))
        
        # Filter messages (in production, this would query the database)
        filtered_messages = messages_store
        
        if session_id:
            filtered_messages = [m for m in filtered_messages if m.get('session_id') == session_id]
        if msg_type:
            filtered_messages = [m for m in filtered_messages if m.get('msg_type') == msg_type]
        
        # Pagination
        paginated = filtered_messages[offset:offset + limit]
        
        return jsonify({
            'messages': paginated,
            'total': len(filtered_messages),
            'limit': limit,
            'offset': offset
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/v1/messages/<message_id>', methods=['GET'])
def get_message(message_id):
    """Get a specific FIX message by ID"""
    try:
        # In production, query database
        message = next((m for m in messages_store if m.get('id') == message_id), None)
        
        if not message:
            return jsonify({'error': 'Message not found'}), 404
        
        return jsonify(message)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/v1/sessions', methods=['GET'])
def get_sessions():
    """Get active FIX sessions"""
    try:
        # Extract unique sessions from messages
        sessions = {}
        for msg in messages_store:
            session_id = msg.get('session_id')
            if session_id:
                if session_id not in sessions:
                    sessions[session_id] = {
                        'session_id': session_id,
                        'sender_comp_id': msg.get('sender_comp_id'),
                        'target_comp_id': msg.get('target_comp_id'),
                        'message_count': 0,
                        'last_message_time': None
                    }
                sessions[session_id]['message_count'] += 1
                msg_time = msg.get('timestamp')
                if msg_time and (not sessions[session_id]['last_message_time'] or 
                                 msg_time > sessions[session_id]['last_message_time']):
                    sessions[session_id]['last_message_time'] = msg_time
        
        return jsonify({
            'sessions': list(sessions.values()),
            'total': len(sessions)
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/v1/alerts', methods=['GET'])
def get_alerts():
    """Get security alerts"""
    try:
        severity = request.args.get('severity')
        start_time = request.args.get('start_time')
        limit = int(request.args.get('limit', 100))
        
        filtered_alerts = alerts_store
        if severity:
            filtered_alerts = [a for a in filtered_alerts if a.get('severity') == severity]
        
        return jsonify({
            'alerts': filtered_alerts[:limit],
            'total': len(filtered_alerts)
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/v1/compliance', methods=['GET'])
def get_compliance_status():
    """Get compliance status"""
    try:
        session_id = request.args.get('session_id')
        
        filtered_results = compliance_results
        if session_id:
            filtered_results = [r for r in filtered_results if r.get('session_id') == session_id]
        
        # Calculate compliance statistics
        total_checks = len(filtered_results)
        compliant_checks = sum(1 for r in filtered_results if r.get('compliant'))
        compliance_rate = (compliant_checks / total_checks * 100) if total_checks > 0 else 100
        
        return jsonify({
            'compliance_rate': compliance_rate,
            'total_checks': total_checks,
            'compliant_checks': compliant_checks,
            'violations': total_checks - compliant_checks,
            'results': filtered_results
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/v1/stats', methods=['GET'])
def get_statistics():
    """Get platform statistics"""
    try:
        # Calculate statistics
        total_messages = len(messages_store)
        active_sessions = len(set(m.get('session_id') for m in messages_store if m.get('session_id')))
        total_alerts = len(alerts_store)
        critical_alerts = sum(1 for a in alerts_store if a.get('severity') == 'CRITICAL')
        
        # Message type distribution
        msg_type_dist = {}
        for msg in messages_store:
            msg_type = msg.get('msg_type', 'UNKNOWN')
            msg_type_dist[msg_type] = msg_type_dist.get(msg_type, 0) + 1
        
        return jsonify({
            'total_messages': total_messages,
            'active_sessions': active_sessions,
            'total_alerts': total_alerts,
            'critical_alerts': critical_alerts,
            'message_type_distribution': msg_type_dist,
            'timestamp': datetime.now().isoformat()
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/v1/messages', methods=['POST'])
def create_message():
    """Create a new FIX message (for testing)"""
    try:
        data = request.get_json()
        message = {
            'id': f"msg_{len(messages_store) + 1}",
            'timestamp': datetime.now().isoformat(),
            **data
        }
        messages_store.append(message)
        return jsonify(message), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
