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


def load_dummy_data():
    """Load dummy data for viewing reports and testing the API."""
    base_time = datetime.now() - timedelta(hours=24)

    # Dummy FIX messages (orders, executions, logon/logout)
    global messages_store
    messages_store = [
        {'id': 'msg_1', 'session_id': 'SESSION-001', 'msg_type': 'D', 'sender_comp_id': 'CLIENT_A', 'target_comp_id': 'BROKER_X',
         'timestamp': (base_time + timedelta(minutes=10)).isoformat(), 'symbol': 'AAPL', 'side': '1', 'order_qty': 100.0, 'price': 175.50, 'cl_ord_id': 'ORD-001', 'ord_type': '2'},
        {'id': 'msg_2', 'session_id': 'SESSION-001', 'msg_type': '8', 'sender_comp_id': 'BROKER_X', 'target_comp_id': 'CLIENT_A',
         'timestamp': (base_time + timedelta(minutes=11)).isoformat(), 'symbol': 'AAPL', 'order_id': 'EX-001', 'exec_type': '0', 'ord_status': '0', 'last_qty': 100.0, 'last_px': 175.50},
        {'id': 'msg_3', 'session_id': 'SESSION-001', 'msg_type': 'D', 'sender_comp_id': 'CLIENT_A', 'target_comp_id': 'BROKER_X',
         'timestamp': (base_time + timedelta(minutes=30)).isoformat(), 'symbol': 'MSFT', 'side': '2', 'order_qty': 250.0, 'price': 380.25, 'cl_ord_id': 'ORD-002', 'ord_type': '2'},
        {'id': 'msg_4', 'session_id': 'SESSION-002', 'msg_type': 'D', 'sender_comp_id': 'CLIENT_B', 'target_comp_id': 'BROKER_X',
         'timestamp': (base_time + timedelta(hours=1)).isoformat(), 'symbol': 'GOOGL', 'side': '1', 'order_qty': 50.0, 'price': 140.00, 'cl_ord_id': 'ORD-003', 'ord_type': '1'},
        {'id': 'msg_5', 'session_id': 'SESSION-002', 'msg_type': '8', 'sender_comp_id': 'BROKER_X', 'target_comp_id': 'CLIENT_B',
         'timestamp': (base_time + timedelta(hours=1, minutes=2)).isoformat(), 'symbol': 'GOOGL', 'order_id': 'EX-002', 'exec_type': '4', 'ord_status': '4', 'last_qty': 50.0, 'last_px': 139.95},
        {'id': 'msg_6', 'session_id': 'SESSION-001', 'msg_type': 'D', 'sender_comp_id': 'CLIENT_A', 'target_comp_id': 'BROKER_X',
         'timestamp': (base_time + timedelta(hours=2)).isoformat(), 'symbol': 'AAPL', 'side': '1', 'order_qty': 500.0, 'price': 174.00, 'cl_ord_id': 'ORD-004', 'ord_type': '2'},
        {'id': 'msg_7', 'session_id': 'SESSION-003', 'msg_type': 'D', 'sender_comp_id': 'CLIENT_C', 'target_comp_id': 'BROKER_X',
         'timestamp': (base_time + timedelta(hours=3)).isoformat(), 'symbol': 'AMZN', 'side': '2', 'order_qty': 75.0, 'price': 185.20, 'cl_ord_id': 'ORD-005', 'ord_type': '2'},
        {'id': 'msg_8', 'session_id': 'SESSION-003', 'msg_type': '8', 'sender_comp_id': 'BROKER_X', 'target_comp_id': 'CLIENT_C',
         'timestamp': (base_time + timedelta(hours=3, minutes=1)).isoformat(), 'symbol': 'AMZN', 'order_id': 'EX-003', 'exec_type': '0', 'ord_status': '2', 'last_qty': 75.0, 'last_px': 185.20},
    ]

    # Dummy security alerts
    global alerts_store
    alerts_store = [
        {'id': 'alert_1', 'severity': 'HIGH', 'type': 'RATE_LIMIT', 'message': 'Unusual message rate from CLIENT_A (session SESSION-001)', 'timestamp': (base_time + timedelta(minutes=35)).isoformat(), 'session_id': 'SESSION-001'},
        {'id': 'alert_2', 'severity': 'MEDIUM', 'type': 'LARGE_ORDER', 'message': 'Order size 500 exceeds typical threshold for symbol AAPL', 'timestamp': (base_time + timedelta(hours=2, minutes=1)).isoformat(), 'session_id': 'SESSION-001'},
        {'id': 'alert_3', 'severity': 'CRITICAL', 'type': 'INVALID_MSG', 'message': 'Malformed FIX message rejected - checksum mismatch', 'timestamp': (base_time + timedelta(hours=4)).isoformat(), 'session_id': 'SESSION-002'},
        {'id': 'alert_4', 'severity': 'LOW', 'type': 'SESSION', 'message': 'New session established: SESSION-003', 'timestamp': (base_time + timedelta(hours=2, minutes=55)).isoformat(), 'session_id': 'SESSION-003'},
    ]

    # Dummy compliance check results (MiFID II, FINRA, etc.)
    global compliance_results
    compliance_results = [
        {'id': 'comp_1', 'session_id': 'SESSION-001', 'rule': 'MiFID II - Best Execution', 'compliant': True, 'checked_at': (base_time + timedelta(minutes=15)).isoformat(), 'details': 'Order executed within price tolerance'},
        {'id': 'comp_2', 'session_id': 'SESSION-001', 'rule': 'FINRA - Order Marking', 'compliant': True, 'checked_at': (base_time + timedelta(minutes=15)).isoformat(), 'details': 'Order correctly marked as agency'},
        {'id': 'comp_3', 'session_id': 'SESSION-002', 'rule': 'MiFID II - Best Execution', 'compliant': True, 'checked_at': (base_time + timedelta(hours=1, minutes=5)).isoformat(), 'details': 'Execution within spread'},
        {'id': 'comp_4', 'session_id': 'SESSION-002', 'rule': 'SEC - Timestamp Accuracy', 'compliant': False, 'checked_at': (base_time + timedelta(hours=4, minutes=1)).isoformat(), 'details': 'Message timestamp outside acceptable drift'},
        {'id': 'comp_5', 'session_id': 'SESSION-003', 'rule': 'MiFID II - Best Execution', 'compliant': True, 'checked_at': (base_time + timedelta(hours=3, minutes=5)).isoformat(), 'details': 'Fill at or better than limit'},
        {'id': 'comp_6', 'session_id': 'SESSION-003', 'rule': 'FINRA - Order Marking', 'compliant': True, 'checked_at': (base_time + timedelta(hours=3, minutes=5)).isoformat(), 'details': 'Correct side and capacity'},
    ]


# Load dummy data when the app starts
load_dummy_data()


@app.route('/')
def index():
    """Root endpoint - API info and links to main endpoints"""
    return jsonify({
        'service': 'FIX Security Platform API',
        'version': '1.0.0',
        'status': 'running',
        'docs': 'See docs/API.md',
        'endpoints': {
            'health': 'GET /api/v1/health',
            'messages': 'GET /api/v1/messages',
            'message_by_id': 'GET /api/v1/messages/<id>',
            'sessions': 'GET /api/v1/sessions',
            'alerts': 'GET /api/v1/alerts',
            'compliance': 'GET /api/v1/compliance',
            'stats': 'GET /api/v1/stats',
        }
    })


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
